package com.faiyaz.project.fittrack.exercise.repository;

import com.faiyaz.project.fittrack.exercise.entity.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, UUID> {
}
