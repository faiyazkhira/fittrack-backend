package com.faiyaz.project.fittrack.exercise.service;

import com.faiyaz.project.fittrack.exception.AccessDeniedException;
import com.faiyaz.project.fittrack.exception.ExerciseNotFoundException;
import com.faiyaz.project.fittrack.exception.InvalidExerciseInputException;
import com.faiyaz.project.fittrack.exception.WorkoutNotFoundException;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseProgressResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseUpdateRequestDto;
import com.faiyaz.project.fittrack.exercise.entity.*;
import com.faiyaz.project.fittrack.exercise.repository.CustomExerciseRepository;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseCatalogRepository;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseRepository;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Transactional
    public List<ExerciseResponseDto> persistExercises(UUID workoutId, ExerciseRequestDto request, UUID userId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout not found for id: " + workoutId));

        if(!workout.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to add exercises to this workout");
        }

        List<Exercise> exercises = request.getExercises().stream()
                .map(ex -> {
                    ExerciseCatalog catalog = null;
                    CustomExercise custom = null;

                    if(ex.getCustomId() != null && ex.getCatalogId() != null){
                        throw new InvalidExerciseInputException("Only one of catalogId or customId must be provided.");
                    }

                    if(ex.getCatalogId() != null){
                        catalog = exerciseCatalogRepository.findById(ex.getCatalogId())
                                .orElseThrow(() -> new ExerciseNotFoundException("Catalog exercise not found for id: " + ex.getCatalogId()));
                    } else if(ex.getCustomId() != null){
                        custom = customExerciseRepository.findById(ex.getCustomId())
                                .orElseThrow(()-> new ExerciseNotFoundException("Custom exercise not found for id: " + ex.getCustomId()));
                    } else {
                        throw new InvalidExerciseInputException("Either catalogId or customId must be provided.");
                    }

                   Exercise e = Exercise.builder()
                            .workout(workout)
                            .exerciseCatalog(catalog)
                            .customExercise(custom)
                            .build();

                    List<ExerciseSet> sets = ex.getSets().stream()
                            .map(s -> ExerciseSet.builder()
                                    .reps(s.getReps())
                                    .weight(s.getWeight())
                                    .notes(s.getNotes())
                                    .loggedAt(LocalDateTime.now())
                                    .exercise(e)
                                    .build())
                            .toList();

                    //exerciseSetRepository.saveAll(sets); We already have cascade.ALL in Exercise Entity

                    e.setSets(sets);
                    return e;
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
                            .sets(ex.getSets().stream()
                                    .map(s -> new ExerciseResponseDto.SetResponse(s.getId(), s.getReps(), s.getWeight(), s.getNotes(), s.getLoggedAt())).toList())
                            .muscleGroup(ex.getExerciseCatalog() != null
                                    ? ex.getExerciseCatalog().getMuscleGroup()
                                    : ex.getCustomExercise().getMuscleGroup())
                            .workoutId(ex.getWorkout().getId())
                            .build();
                }).collect(Collectors.toList());
    }

    public ExerciseResponseDto updateExercise(UUID id, UUID userId, ExerciseUpdateRequestDto request) {
        log.info("User {} attempting to update exercise {}", userId, id);

        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found for id: " + id));

        if(!existingExercise.getWorkout().getUser().getId().equals(userId)){
            log.warn("Access denied: User {} tried to update exercise {}", userId, id);
            throw new AccessDeniedException("You do not have permission to update this exercise");
        }

        if(request.getCatalogId() != null && request.getCustomId() != null){
            throw new InvalidExerciseInputException("Only one of catalogId or customId must be provided.");
        }

        if(request.getCatalogId() != null){
            ExerciseCatalog catalog = exerciseCatalogRepository.findById(request.getCatalogId())
                    .orElseThrow(() -> new ExerciseNotFoundException("Catalog exercise not found for id: " + request.getCatalogId()));
            existingExercise.setExerciseCatalog(catalog);
            existingExercise.setCustomExercise(null);
        } else if(request.getCustomId() != null){
            CustomExercise custom = customExerciseRepository.findById(request.getCustomId())
                    .orElseThrow(() -> new ExerciseNotFoundException("Custom exercise not found for id: " + request.getCustomId()));
            existingExercise.setCustomExercise(custom);
            existingExercise.setExerciseCatalog(null);
        }

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
                .sets(saved.getSets().stream()
                        .map(s -> new ExerciseResponseDto.SetResponse(s.getId(), s.getReps(), s.getWeight(), s.getNotes(), s.getLoggedAt()))
                        .toList())
                .muscleGroup(muscleGroup)
                .workoutId(saved.getWorkout().getId())
                .build();

    }

    public void deleteExercise(UUID id, UUID userId) throws AccessDeniedException {
        log.info("User {} attempting to delete exercise {}", userId, id);

        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found for id: " + id));

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

        List<Exercise> combined = Stream.concat(fromCatalog.stream(), fromCustom.stream())
                .toList();

       return combined.stream().map(e -> {
            String exerciseName = e.getExerciseCatalog() != null
                    ? e.getExerciseCatalog().getName()
                    : e.getCustomExercise().getName();

            double totalVolume = e.getSets().stream()
                    .mapToDouble(s-> s.getReps() * s.getWeight()).sum();

                   return ExerciseProgressResponseDto.builder()
                            .date(e.getWorkout().getSessionDate())
                            .name(exerciseName)
                            .sets(e.getSets().stream()
                                    .map(s -> new ExerciseResponseDto.SetResponse(s.getId(), s.getReps(), s.getWeight(), s.getNotes(), s.getLoggedAt()))
                                    .toList())
                            .volume(totalVolume)
                            .build();
                }).toList();
    }
}
