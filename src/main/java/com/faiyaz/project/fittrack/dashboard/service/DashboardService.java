package com.faiyaz.project.fittrack.dashboard.service;

import com.faiyaz.project.fittrack.dashboard.dto.DashboardResponseDto;
import com.faiyaz.project.fittrack.exercise.entity.Exercise;
import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseRepository;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import com.faiyaz.project.fittrack.workout.entity.Workout;
import com.faiyaz.project.fittrack.workout.repository.WorkoutRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public DashboardService(WorkoutRepository workoutRepository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public DashboardResponseDto getDashboard(UUID userId){
        int totalWorkouts = workoutRepository.countByUserId(userId);
        int workoutsThisMonth = workoutRepository.countByUserIdAndSessionDateBetween(
                userId,
                YearMonth.now().atDay(1),
                LocalDate.now()
        );

        double currentWeight = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Username not found")).getWeight();

        Workout lastWorkout = workoutRepository.findTopByUserIdOrderBySessionDateDesc(userId);
        List<DashboardResponseDto.ExerciseSummary> exerciseSummaryList = new ArrayList<>();
        Set<MuscleGroup> muscleGroups = new HashSet<>();
        String muscleGroupDisplay = "";

        if(lastWorkout != null){
            List<Exercise> exercises = exerciseRepository.findByWorkoutId(lastWorkout.getId());
            for(Exercise ex : exercises){
                muscleGroups.add(ex.getExerciseCatalog() != null ? ex.getExerciseCatalog().getMuscleGroup() : ex.getCustomExercise().getMuscleGroup());
                exerciseSummaryList.add(DashboardResponseDto.ExerciseSummary.builder()
                        .name(ex.getExerciseCatalog() != null
                                ? ex.getExerciseCatalog().getName()
                                : ex.getCustomExercise().getName()).build()
                );
            }
            muscleGroupDisplay = muscleGroups.stream().sorted()
                    .map(MuscleGroup::toString)
                    .collect(Collectors.joining(", "));
        }

        List<Object[]> heatmapData = workoutRepository.getWorkoutCountGroupedByDate(userId);
        List<DashboardResponseDto.CalendarHeatmapDto> heatmap = heatmapData.stream()
                .map(obj -> DashboardResponseDto.CalendarHeatmapDto.builder()
                        .date((LocalDate) obj[0])
                        .count(((Long) obj[1]).intValue())
                        .build())
                .toList();

        return DashboardResponseDto.builder()
                .workoutsThisMonth(workoutsThisMonth)
                .totalWorkouts(totalWorkouts)
                .currentWeight(currentWeight)
                .lastWorkout(lastWorkout != null
                        ? DashboardResponseDto.LastWorkoutDto.builder()
                        .date(lastWorkout.getSessionDate())
                        .muscleGroup(muscleGroupDisplay)
                        .exercises(exerciseSummaryList)
                        .build() : null)
                .calendarHeatmap(heatmap)
                .build();
    }
}
