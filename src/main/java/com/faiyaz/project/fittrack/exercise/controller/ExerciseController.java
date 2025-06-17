package com.faiyaz.project.fittrack.exercise.controller;

import com.faiyaz.project.fittrack.exercise.dto.ExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.service.ExerciseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
