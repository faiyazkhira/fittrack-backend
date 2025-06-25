package com.faiyaz.project.fittrack.user.repository;

import com.faiyaz.project.fittrack.user.entity.BodyWeightLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BodyWeightRepository extends JpaRepository<BodyWeightLog, UUID> {
    List<BodyWeightLog> findByUser_IdOrderByDateLoggedAsc(UUID userId);
    List<BodyWeightLog> findByUser_IdAndDateLoggedBetweenOrderByDateLoggedAsc(UUID userId, LocalDate startDate, LocalDate endDate);
}
