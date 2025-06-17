package com.faiyaz.project.fittrack.workout.service;

import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import com.faiyaz.project.fittrack.workout.dto.WorkoutRequestDto;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
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
}
