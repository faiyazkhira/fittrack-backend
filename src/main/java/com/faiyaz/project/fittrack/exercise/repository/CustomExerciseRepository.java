package com.faiyaz.project.fittrack.exercise.repository;

import com.faiyaz.project.fittrack.exercise.entity.CustomExercise;
import com.faiyaz.project.fittrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomExerciseRepository extends JpaRepository<CustomExercise, UUID> {
    List<CustomExercise> findByUser(User user);
}
