package com.faiyaz.project.fittrack.exercise.service;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseProgressResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseUpdateRequestDto;
import com.faiyaz.project.fittrack.exercise.entity.CustomExercise;
import com.faiyaz.project.fittrack.exercise.entity.Exercise;
import com.faiyaz.project.fittrack.exercise.entity.ExerciseCatalog;
import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import com.faiyaz.project.fittrack.exercise.repository.CustomExerciseRepository;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseCatalogRepository;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseRepository;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseCatalogRepository exerciseCatalogRepository;
    private final CustomExerciseRepository customExerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository,
                           ExerciseCatalogRepository exerciseCatalogRepository,
                           CustomExerciseRepository customExerciseRepository
                           ) {
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseCatalogRepository = exerciseCatalogRepository;
        this.customExerciseRepository = customExerciseRepository;
    }

    public List<ExerciseResponseDto> addExerciseToWorkout(ExerciseRequestDto request){
        Workout workout = workoutRepository.findById(request.getWorkoutId())
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));

        List<Exercise> exercises = request.getExercises().stream()
                .map(ex -> {
                    ExerciseCatalog catalog = null;
                    CustomExercise custom = null;

                    if(ex.getCustomId() != null && ex.getCatalogId() != null){
                        throw new IllegalArgumentException("Only one of catalogId or customId must be provided.");
                    }

                    if(ex.getCatalogId() != null){
                        catalog = exerciseCatalogRepository.findById(ex.getCatalogId())
                                .orElseThrow(() -> new NoSuchElementException("Catalog exercise not found"));
                    } else if(ex.getCustomId() != null){
                        custom = customExerciseRepository.findById(ex.getCustomId())
                                .orElseThrow(()-> new NoSuchElementException("Custom exercise not found"));
                    } else {
                        throw new IllegalArgumentException("Either catalogId or customId must be provided.");
                    }

                   return Exercise.builder()
                            .workout(workout)
                            .exerciseCatalog(catalog)
                            .customExercise(custom)
                            .sets(ex.getSets())
                            .reps(ex.getReps())
                            .weight(ex.getWeight())
                            .build();
                }).collect(Collectors.toList());

        List<Exercise> saved = exerciseRepository.saveAll(exercises);

        return saved.stream()
                .map(ex -> {
                    String name = ex.getExerciseCatalog() != null
                            ? ex.getExerciseCatalog().getName()
                            : ex.getCustomExercise().getName();

                    return ExerciseResponseDto.builder()
                            .id(ex.getId())
                            .name(name)
                            .sets(ex.getSets())
                            .reps(ex.getReps())
                            .weight(ex.getWeight())
                            .muscleGroup(ex.getExerciseCatalog() != null
                                    ? ex.getExerciseCatalog().getMuscleGroup()
                                    : ex.getCustomExercise().getMuscleGroup())
                            .workoutId(ex.getWorkout().getId())
                            .build();
                }).collect(Collectors.toList());
    }

    public ExerciseResponseDto updateExercise(UUID id, UUID userId, ExerciseUpdateRequestDto request) throws AccessDeniedException {
        log.info("User {} attempting to update exercise {}", userId, id);

        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Exercise not found"));

        if(!existingExercise.getWorkout().getUser().getId().equals(userId)){
            log.warn("Access denied: User {} tried to update exercise {}", userId, id);
            throw new AccessDeniedException("You do not have permission to update this exercise");
        }

        if(request.getCatalogId() != null && request.getCustomId() != null){
            throw new IllegalArgumentException("Only one of catalogId or customId must be provided.");
        }

        if(request.getCatalogId() != null){
            ExerciseCatalog catalog = exerciseCatalogRepository.findById(request.getCatalogId())
                    .orElseThrow(() -> new NoSuchElementException("Catalog exercise not found"));
            existingExercise.setExerciseCatalog(catalog);
            existingExercise.setCustomExercise(null);
        } else if(request.getCustomId() != null){
            CustomExercise custom = customExerciseRepository.findById(request.getCustomId())
                    .orElseThrow(() -> new NoSuchElementException("Custom exercise not found"));
            existingExercise.setCustomExercise(custom);
            existingExercise.setExerciseCatalog(null);
        }


        if(request.getSets() != null) existingExercise.setSets(request.getSets());
        if(request.getReps() != null) existingExercise.setReps(request.getReps());
        if(request.getWeight() != null) existingExercise.setWeight(request.getWeight());


        Exercise saved = exerciseRepository.save(existingExercise);

        String name = saved.getExerciseCatalog() != null
                ? saved.getExerciseCatalog().getName()
                : saved.getCustomExercise().getName();

        MuscleGroup muscleGroup = saved.getExerciseCatalog() != null
                ? saved.getExerciseCatalog().getMuscleGroup()
                : saved.getCustomExercise().getMuscleGroup();

        return ExerciseResponseDto.builder()
                .id(saved.getId())
                .name(name)
                .sets(saved.getSets())
                .reps(saved.getReps())
                .weight(saved.getWeight())
                .muscleGroup(muscleGroup)
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
        LocalDate start = startDate != null ? startDate : LocalDate.of(1970, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<Exercise> fromCatalog = exerciseRepository.findByWorkout_User_IdAndExerciseCatalog_NameAndWorkout_SessionDateBetweenOrderByWorkout_SessionDateAsc(
                userId, name, start, end);
        List<Exercise> fromCustom = exerciseRepository.findByWorkout_User_IdAndCustomExercise_NameAndWorkout_SessionDateBetweenOrderByWorkout_SessionDateAsc(
                userId, name, start, end);

        List<Exercise> combined = new ArrayList<>();
        combined.addAll(fromCatalog);
        combined.addAll(fromCustom);

        List<ExerciseProgressResponseDto> response = combined.stream().map(e -> {
            String exerciseName = e.getExerciseCatalog() != null
                    ? e.getExerciseCatalog().getName()
                    : e.getCustomExercise().getName();

                   return ExerciseProgressResponseDto.builder()
                            .date(e.getWorkout().getSessionDate())
                            .name(exerciseName)
                            .sets(e.getSets())
                            .reps(e.getReps())
                            .weight(e.getWeight())
                            .volume(e.getSets() * e.getReps() * e.getWeight())
                            .build();
                }).toList();

        return response;
    }
}
