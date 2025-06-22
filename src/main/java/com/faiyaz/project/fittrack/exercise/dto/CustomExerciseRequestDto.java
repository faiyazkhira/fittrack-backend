package com.faiyaz.project.fittrack.exercise.dto;

import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomExerciseRequestDto {

    @NotBlank(message = "Exercise name cannot be blank")
    private String name;
    @NotNull(message = "Muscle group cannot be null")
    private MuscleGroup muscleGroup;
}
