package com.faiyaz.project.fittrack.exercise.dto;

import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseRequestDto {

    private UUID workoutId;
    private List<ExerciseInput> exercises;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExerciseInput {
        private UUID catalogId;
        private UUID customId;
        private List<SetInput> sets;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SetInput {
            private Integer reps;
            private Double weight;
            private String notes;
        }
    }
}
