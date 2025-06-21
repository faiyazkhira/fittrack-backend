package com.faiyaz.project.fittrack.exercise.controller;

import com.faiyaz.project.fittrack.exercise.dto.CustomExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.CustomExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.service.CustomExerciseService;
import com.faiyaz.project.fittrack.user.entity.User;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/custom-exercises")
public class CustomExerciseController {

    private final CustomExerciseService customExerciseService;

    public CustomExerciseController(CustomExerciseService customExerciseService) {
        this.customExerciseService = customExerciseService;
    }

    @PostMapping
    public ResponseEntity<CustomExerciseResponseDto> create(@RequestBody CustomExerciseRequestDto request,
                                                            @AuthenticationPrincipal User user){
        return ResponseEntity.ok(customExerciseService.createCustomExercise(request, user));
    }

    @GetMapping
    public ResponseEntity<List<CustomExerciseResponseDto>> getAll(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(customExerciseService.getAll(user));
    }
}
