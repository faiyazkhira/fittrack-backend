package com.faiyaz.project.fittrack.exercise.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseProgressResponseDto {
    private LocalDate date;
    private String name;
    private List<ExerciseResponseDto.SetResponse> sets;
    private double volume;
}
