package com.faiyaz.project.fittrack.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetUpdateRequestDto {
    private Integer reps;
    private Double weight;
    private String notes;
}
