package com.faiyaz.project.fittrack.exercise.controller;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseProgressResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseUpdateRequestDto;
import com.faiyaz.project.fittrack.exercise.service.ExerciseService;
import com.faiyaz.project.fittrack.user.entity.User;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping
    public ResponseEntity<List<ExerciseResponseDto>> addExercises(@RequestBody ExerciseRequestDto request){
        List<ExerciseResponseDto> saved = exerciseService.addExerciseToWorkout(request);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> updateExercise(@PathVariable UUID id,
                                                              @RequestBody ExerciseUpdateRequestDto request,
                                                              @AuthenticationPrincipal User user) throws AccessDeniedException {
        ExerciseResponseDto updated = exerciseService.updateExercise(id, user.getId(), request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExercise(@PathVariable UUID id,
                                            @AuthenticationPrincipal User user) throws AccessDeniedException {
        exerciseService.deleteExercise(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/progress")
    public ResponseEntity<List<ExerciseProgressResponseDto>> getExerciseProgress(
            @AuthenticationPrincipal User user,
            @RequestParam String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        List<ExerciseProgressResponseDto> response = exerciseService.getExerciseProgress(user.getId(), name, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
