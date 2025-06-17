package com.faiyaz.project.fittrack.exercise.service;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.entity.Exercise;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseRepository;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
    }

    public List<ExerciseResponseDto> addExerciseToWorkout(ExerciseRequestDto request){
        Workout workout = workoutRepository.findById(request.getWorkoutId())
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));

        List<Exercise> exercises = request.getExercises().stream()
                .map(ex -> Exercise.builder()
                        .workout(workout)
                        .name(ex.getName())
                        .sets(ex.getSets())
                        .reps(ex.getReps())
                        .weight(ex.getWeight())
                        .muscleGroup(ex.getMuscleGroup())
                        .build())
                .collect(Collectors.toList());

        List<Exercise> saved = exerciseRepository.saveAll(exercises);

        return saved.stream()
                .map(ex -> ExerciseResponseDto.builder()
                        .id(ex.getId())
                        .name(ex.getName())
                        .sets(ex.getSets())
                        .reps(ex.getReps())
                        .weight(ex.getWeight())
                        .muscleGroup(ex.getMuscleGroup())
                        .workoutId(ex.getWorkout().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
