package com.faiyaz.project.fittrack.exercise.dto;

import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseResponseDto {

    private UUID id;
    private String name;
    private List<SetResponse> sets;
    private MuscleGroup muscleGroup;
    private UUID workoutId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetResponse {
        private UUID id;
        private int reps;
        private double weight;
        private String notes;
        private LocalDateTime loggedAt;
    }
}
