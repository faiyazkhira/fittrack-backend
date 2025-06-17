package com.faiyaz.project.fittrack.exercise.repository;

import com.faiyaz.project.fittrack.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

    List<Exercise> findByWorkout_User_IdAndNameOrderByWorkout_SessionDateAsc(UUID userId, String name);

}
