package com.faiyaz.project.fittrack.workout.repository;

import com.faiyaz.project.fittrack.workout.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

    @Query("SELECT w.sessionDate, COUNT(w) FROM Workout w WHERE w.user.id = :userId GROUP BY w.sessionDate")
    List<Object[]> getWorkoutCountGroupedByDate(@Param("userId") UUID userId);

    List<Workout> findByUserIdOrderBySessionDateDesc(UUID userId);
    List<Workout> findByUserIdAndSessionDateBetween(UUID userId, LocalDate start, LocalDate end);
    Workout findTopByUserIdOrderBySessionDateDesc(UUID userId);
    int countByUserId(UUID userId);
    int countByUserIdAndSessionDateBetween(UUID userId, LocalDate start, LocalDate end);
}
