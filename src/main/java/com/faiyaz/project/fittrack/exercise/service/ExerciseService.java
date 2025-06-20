package com.faiyaz.project.fittrack.exercise.service;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseProgressResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseUpdateRequestDto;
import com.faiyaz.project.fittrack.exercise.entity.Exercise;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseRepository;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
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

    public ExerciseResponseDto updateExercise(UUID id, UUID userId, ExerciseUpdateRequestDto request) throws AccessDeniedException {
        log.info("User {} attempting to update exercise {}", userId, id);

        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Exercise not found"));

        if(!existingExercise.getWorkout().getUser().getId().equals(userId)){
            log.warn("Access denied: User {} tried to update exercise {}", userId, id);
            throw new AccessDeniedException("You do not have permission to update this exercise");
        }

        if(request.getName() != null) existingExercise.setName(request.getName());
        if(request.getSets() != null) existingExercise.setSets(request.getSets());
        if(request.getReps() != null) existingExercise.setReps(request.getReps());
        if(request.getWeight() != null) existingExercise.setWeight(request.getWeight());
        if(request.getMuscleGroup() != null) existingExercise.setMuscleGroup(request.getMuscleGroup());

        Exercise saved = exerciseRepository.save(existingExercise);

        return ExerciseResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .sets(saved.getSets())
                .reps(saved.getReps())
                .weight(saved.getWeight())
                .muscleGroup(saved.getMuscleGroup())
                .workoutId(saved.getWorkout().getId())
                .build();

    }

    public void deleteExercise(UUID id, UUID userId) throws AccessDeniedException {
        log.info("User {} attempting to delete exercise {}", userId, id);

        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Exercise not found"));

        if(!existingExercise.getWorkout().getUser().getId().equals(userId)){
            log.warn("Access denied: User {} tried to delete exercise {}", userId, id);
            throw new AccessDeniedException("You do not have permission to delete this exercise");
        }

        exerciseRepository.delete(existingExercise);
    }

    public List<ExerciseProgressResponseDto> getExerciseProgress(UUID userId, String name, LocalDate startDate, LocalDate endDate) {
        List<Exercise> exercises = exerciseRepository.findByWorkout_User_IdAndNameAndWorkout_SessionDateBetweenOrderByWorkout_SessionDateAsc(
                userId,
                name,
                startDate != null ? startDate : LocalDate.of(1970, 1, 1),
                endDate != null ? endDate : LocalDate.now()
        );

        List<ExerciseProgressResponseDto> response = exercises.stream().map(e -> ExerciseProgressResponseDto.builder()
                .date(e.getWorkout().getSessionDate())
                .name(e.getName())
                .sets(e.getSets())
                .reps(e.getReps())
                .weight(e.getWeight())
                .volume(e.getSets()*e.getReps()*e.getWeight())
                .build())
                .toList();

        return response;
    }
}
