package com.faiyaz.project.fittrack.workout.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutResponseDto {

    private UUID id;
    private LocalDate sessionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userEmail;
    private UUID userId;
}
