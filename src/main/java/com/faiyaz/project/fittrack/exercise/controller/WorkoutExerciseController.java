package com.faiyaz.project.fittrack.exercise.controller;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.service.ExerciseService;
import com.faiyaz.project.fittrack.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workouts/{workoutId}/exercises")
public class WorkoutExerciseController {

    private final ExerciseService exerciseService;

    public WorkoutExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<List<ExerciseResponseDto>> replaceExercises(@Valid @RequestBody ExerciseRequestDto request,
                                                                      @AuthenticationPrincipal User user,
                                                                      @PathVariable UUID workoutId) {
        List<ExerciseResponseDto> saved = exerciseService.persistExercises(workoutId, request, user.getId());
        return ResponseEntity.ok(saved);
    }
}
