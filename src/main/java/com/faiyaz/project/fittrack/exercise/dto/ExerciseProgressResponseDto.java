package com.faiyaz.project.fittrack.exercise.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseProgressResponseDto {
    private LocalDate date;
    private String name;
    private int sets;
    private int reps;
    private double weight;
    private double volume;
}
