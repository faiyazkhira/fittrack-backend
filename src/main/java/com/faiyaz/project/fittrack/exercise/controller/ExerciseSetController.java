package com.faiyaz.project.fittrack.exercise.controller;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.SetUpdateRequestDto;
import com.faiyaz.project.fittrack.exercise.service.ExerciseSetService;
import com.faiyaz.project.fittrack.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseSetController {

    private final ExerciseSetService setService;

    public ExerciseSetController(ExerciseSetService setService) {
        this.setService = setService;
    }

    @PatchMapping("/sets/{setId}")
    public ResponseEntity<ExerciseResponseDto.SetResponse> updateSet(@PathVariable UUID setId,
                                                                     @AuthenticationPrincipal User user,
                                                                     @RequestBody SetUpdateRequestDto request) {
        return ResponseEntity.ok(setService.updateSingleSet(setId, user.getId(), request));
    }

    @DeleteMapping("/sets/{setId}")
    public ResponseEntity<?> deleteSet(@PathVariable UUID setId,
                                       @AuthenticationPrincipal User user) {
        setService.deleteSet(setId, user.getId());
        return  ResponseEntity.noContent().build();
    }
}
