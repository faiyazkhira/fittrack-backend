package com.faiyaz.project.fittrack.workout.controller;

import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.workout.dto.WorkoutRequestDto;
import com.faiyaz.project.fittrack.workout.dto.WorkoutResponseDto;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.service.WorkoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutResponseDto> createWorkout(@RequestBody WorkoutRequestDto request, @AuthenticationPrincipal User user){

        Workout session = workoutService.createSession(user.getId(), request.getSessionDate());

        WorkoutResponseDto response = WorkoutResponseDto.builder()
                .id(session.getId())
                .sessionDate(session.getSessionDate())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .userEmail(session.getUser().getEmail())
                .userId(session.getUser().getId())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDto> updateWorkout(@PathVariable UUID id,
                                                            @AuthenticationPrincipal User user,
                                                            @RequestBody WorkoutRequestDto request) throws AccessDeniedException {
        WorkoutResponseDto updated = workoutService.updateWorkout(id, user.getId(), request.getSessionDate());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkout(@PathVariable UUID id,
                                           @AuthenticationPrincipal User user) throws AccessDeniedException {
        workoutService.deleteWorkout(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
