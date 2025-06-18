package com.faiyaz.project.fittrack.workout.service;

import com.faiyaz.project.fittrack.exercise.repository.ExerciseRepository;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import com.faiyaz.project.fittrack.workout.dto.WorkoutRequestDto;
import com.faiyaz.project.fittrack.workout.dto.WorkoutResponseDto;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public Workout createSession(UUID userId, LocalDate sessionDate){
        if(sessionDate.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Session date cannot be in future");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Workout session = Workout.builder()
                .user(user)
                .sessionDate(sessionDate)
                .build();

        return workoutRepository.save(session);
    }

    public WorkoutResponseDto updateWorkout(UUID id, UUID userId, LocalDate newDate) throws AccessDeniedException {
        if(newDate.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Workout date cannot be in future");
        }

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workout not found"));

        if(!workout.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to edit this workout");
        }

        workout.setSessionDate(newDate);
        Workout saved = workoutRepository.save(workout);

        return WorkoutResponseDto.builder()
                .id(saved.getId())
                .sessionDate(saved.getSessionDate())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .userEmail(saved.getUser().getEmail())
                .userId(saved.getUser().getId())
                .build();
    }

    public void deleteWorkout(UUID id, UUID userId) throws AccessDeniedException {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Workout not found"));

        if(!workout.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to delete this workout");
        }

        exerciseRepository.deleteByWorkoutId(id);
        workoutRepository.delete(workout);
    }
}
