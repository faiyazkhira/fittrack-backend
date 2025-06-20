package com.faiyaz.project.fittrack.exercise.dto;

import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseUpdateRequestDto {
    private UUID catalogId;
    private UUID customId;
    private Integer sets;
    private Integer reps;
    private Double weight;
}
