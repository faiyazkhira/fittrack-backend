package com.faiyaz.project.fittrack.workout.service;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.entity.Exercise;
import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseRepository;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import com.faiyaz.project.fittrack.workout.dto.WorkoutRequestDto;
import com.faiyaz.project.fittrack.workout.dto.WorkoutResponseDto;
import com.faiyaz.project.fittrack.workout.dto.WorkoutWithExercisesResponseDto;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public Workout createSession(UUID userId, LocalDate sessionDate){
        if(sessionDate.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Session date cannot be in future");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Workout session = Workout.builder()
                .user(user)
                .sessionDate(sessionDate)
                .build();

        return workoutRepository.save(session);
    }

    public WorkoutResponseDto updateWorkout(UUID id, UUID userId, LocalDate newDate) throws AccessDeniedException {
        if(newDate.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Workout date cannot be in future");
        }

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workout not found"));

        if(!workout.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to edit this workout");
        }

        workout.setSessionDate(newDate);
        Workout saved = workoutRepository.save(workout);

        return WorkoutResponseDto.builder()
                .id(saved.getId())
                .sessionDate(saved.getSessionDate())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .userEmail(saved.getUser().getEmail())
                .userId(saved.getUser().getId())
                .build();
    }

    public void deleteWorkout(UUID id, UUID userId) throws AccessDeniedException {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workout not found"));

        if(!workout.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to delete this workout");
        }

        exerciseRepository.deleteByWorkoutId(id);
        workoutRepository.delete(workout);
    }

    public Page<WorkoutWithExercisesResponseDto> getFilteredWorkouts(UUID userId, int page, int size, LocalDate startDate, LocalDate endDate, MuscleGroup muscleGroup) {
        //Create the pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by("sessionDate").descending());

        //Fetch the workouts by userId and optional dates
        List<Workout> workouts = workoutRepository.findByUserIdAndSessionDateBetween(
                userId,
                startDate != null ? startDate : LocalDate.of(1970, 1, 1),
                endDate != null ? endDate : LocalDate.now()
        );

        //Filter the exercises
        List<WorkoutWithExercisesResponseDto> response = workouts.stream().map(workout -> {
            List<Exercise> exercises = exerciseRepository.findByWorkoutId(workout.getId());

            if(muscleGroup != null){
                exercises = exercises.stream()
                        .filter(e -> {
                            MuscleGroup muscle = e.getExerciseCatalog() != null
                                    ? e.getExerciseCatalog().getMuscleGroup()
                                    : e.getCustomExercise() != null
                                        ? e.getCustomExercise().getMuscleGroup()
                                        : null;

                            return muscle == muscleGroup;
                        })
                        .toList();
            }

            return new WorkoutWithExercisesResponseDto(
                    workout.getSessionDate(),
                    exercises.stream()
                            .map(e -> {
                                String name = e.getExerciseCatalog() != null
                                        ? e.getExerciseCatalog().getName()
                                        : e.getCustomExercise().getName();

                                MuscleGroup muscle = e.getExerciseCatalog() != null
                                        ? e.getExerciseCatalog().getMuscleGroup()
                                        : e.getCustomExercise().getMuscleGroup();

                                List<ExerciseResponseDto.SetResponse> sets = e.getSets().stream()
                                        .map(s -> new ExerciseResponseDto.SetResponse(s.getId(), s.getReps(), s.getWeight(), s.getNotes(), s.getLoggedAt()))
                                        .toList();
                                return new ExerciseResponseDto(
                                        e.getId(), name, sets, muscle, e.getWorkout().getId()
                                );
                            }).toList()
            );
        }).filter(w -> !w.getExercises().isEmpty()).toList();

        int start = Math.min((int) pageable.getOffset(), response.size());
        int end = Math.min(start + (int) pageable.getPageSize(), response.size());
        List<WorkoutWithExercisesResponseDto> paginatedList = response.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, response.size());
    }
}
