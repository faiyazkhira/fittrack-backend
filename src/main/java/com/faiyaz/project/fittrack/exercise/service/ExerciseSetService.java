package com.faiyaz.project.fittrack.exercise.service;

import com.faiyaz.project.fittrack.exception.AccessDeniedException;
import com.faiyaz.project.fittrack.exception.ExerciseSetNotFoundException;
import com.faiyaz.project.fittrack.exercise.dto.ExerciseResponseDto;
import com.faiyaz.project.fittrack.exercise.dto.SetUpdateRequestDto;
import com.faiyaz.project.fittrack.exercise.entity.ExerciseSet;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExerciseSetService {

    private final ExerciseSetRepository exerciseSetRepository;

    public ExerciseSetService(ExerciseSetRepository exerciseSetRepository) {
        this.exerciseSetRepository = exerciseSetRepository;
    }

    @Transactional
    public ExerciseResponseDto.SetResponse updateSingleSet(UUID setId, UUID userId, SetUpdateRequestDto request) throws AccessDeniedException {
        ExerciseSet existingSet = exerciseSetRepository.findById(setId)
                .orElseThrow(() -> new ExerciseSetNotFoundException("Set not found for id: " + setId));

        if(!existingSet.getExercise().getWorkout().getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to update this set");
        }

        if(request.getReps() != null) existingSet.setReps(request.getReps());
        if(request.getWeight() != null) existingSet.setWeight(request.getWeight());
        if(request.getNotes() != null) existingSet.setNotes(request.getNotes());

        ExerciseSet saved = exerciseSetRepository.save(existingSet);

        return new ExerciseResponseDto.SetResponse(
                saved.getId(),
                saved.getReps(),
                saved.getWeight(),
                saved.getNotes(),
                saved.getLoggedAt());
    }

    public void deleteSet(UUID setId, UUID userId) {
        ExerciseSet set = exerciseSetRepository.findById(setId)
                .orElseThrow(() -> new ExerciseSetNotFoundException("Set not found for id: " + setId));

        if(!set.getExercise().getWorkout().getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to delete this set");
        }

        exerciseSetRepository.delete(set);
    }
}
