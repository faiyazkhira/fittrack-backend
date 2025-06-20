package com.faiyaz.project.fittrack.exercise.repository;

import com.faiyaz.project.fittrack.exercise.entity.ExerciseCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExerciseCatalogRepository extends JpaRepository<ExerciseCatalog, UUID> {
}
