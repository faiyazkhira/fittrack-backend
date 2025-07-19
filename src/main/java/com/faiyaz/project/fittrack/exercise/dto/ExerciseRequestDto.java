package com.faiyaz.project.fittrack.exercise.dto;

import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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


    @Valid
    @NotEmpty(message = "Exercises list cannot be empty")
    private List<ExerciseInput> exercises;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExerciseInput {
        private UUID catalogId;
        private UUID customId;

        @Valid
        @NotEmpty(message = "Each exercise must have at least one set")
        private List<SetInput> sets;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SetInput {

            @NotNull(message = "Reps must be provided")
            @Min(value = 1, message = "Reps must be at least 1")
            private Integer reps;

            @NotNull(message = "Weight must be provided")
            @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be equal to or greater than 0")
            private Double weight;

            @Size(max = 255, message = "Notes must be at most 255 characters")
            private String notes;
        }
    }
}
