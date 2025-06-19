package com.faiyaz.project.fittrack.workout.dto;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutWithExercisesResponseDto {
    private LocalDate sessionDate;
    private List<ExerciseResponseDto> exercises;
}
