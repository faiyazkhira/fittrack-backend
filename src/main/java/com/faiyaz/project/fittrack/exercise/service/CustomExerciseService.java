package com.faiyaz.project.fittrack.exercise.service;

import com.faiyaz.project.fittrack.exercise.dto.CustomExerciseRequestDto;
import com.faiyaz.project.fittrack.exercise.dto.CustomExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.entity.CustomExercise;
import com.faiyaz.project.fittrack.exercise.repository.CustomExerciseRepository;
import com.faiyaz.project.fittrack.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomExerciseService {

    private final CustomExerciseRepository customExerciseRepository;

    public CustomExerciseService(CustomExerciseRepository customExerciseRepository) {
        this.customExerciseRepository = customExerciseRepository;
    }

    public CustomExerciseResponseDto createCustomExercise(CustomExerciseRequestDto request, User user) {
        CustomExercise customExercise = CustomExercise.builder()
                .name(request.getName())
                .user(user)
                .muscleGroup(request.getMuscleGroup())
                .build();

        CustomExercise saved = customExerciseRepository.save(customExercise);

        return new CustomExerciseResponseDto(saved.getId(), saved.getName(), saved.getMuscleGroup());
    }

    public List<CustomExerciseResponseDto> getAll(User user) {
        return customExerciseRepository.findByUser(user).stream()
                .map(e -> new CustomExerciseResponseDto(e.getId(), e.getName(), e.getMuscleGroup()))
                .toList();
    }
}
