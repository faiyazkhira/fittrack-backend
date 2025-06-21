package com.faiyaz.project.fittrack.exercise.repository;

import com.faiyaz.project.fittrack.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

    //List<Exercise> findByWorkout_User_IdAndNameOrderByWorkout_SessionDateAsc(UUID userId, String name);
    @Transactional
    void deleteByWorkoutId(UUID workoutId);

    List<Exercise> findByWorkoutId(UUID workoutId);

    List<Exercise> findByWorkout_User_IdAndExerciseCatalog_NameAndWorkout_SessionDateBetweenOrderByWorkout_SessionDateAsc(
            UUID userId,
            String name,
            LocalDate startDate,
            LocalDate endDate);
    List<Exercise> findByWorkout_User_IdAndCustomExercise_NameAndWorkout_SessionDateBetweenOrderByWorkout_SessionDateAsc(
            UUID userId,
            String name,
            LocalDate startDate,
            LocalDate endDate
    );

}
