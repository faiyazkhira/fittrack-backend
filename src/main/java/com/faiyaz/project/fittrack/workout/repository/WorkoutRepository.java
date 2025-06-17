package com.faiyaz.project.fittrack.workout.repository;

import com.faiyaz.project.fittrack.workout.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
}
