package com.faiyaz.project.fittrack.exercise.repository;

import com.faiyaz.project.fittrack.exercise.entity.CustomExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomExerciseRepository extends JpaRepository<CustomExercise, UUID> {
}
