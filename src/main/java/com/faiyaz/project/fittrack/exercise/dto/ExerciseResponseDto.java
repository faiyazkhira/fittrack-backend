package com.faiyaz.project.fittrack.exercise.dto;

import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseResponseDto {

    private UUID id;
    private String name;
    private int sets;
    private int reps;
    private double weight;
    private MuscleGroup muscleGroup;
    private UUID workoutId;
}
