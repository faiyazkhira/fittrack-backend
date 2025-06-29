package com.faiyaz.project.fittrack.workout.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutRequestDto {

    @NotNull(message = "Date must be provided")
    private LocalDate sessionDate;
}
