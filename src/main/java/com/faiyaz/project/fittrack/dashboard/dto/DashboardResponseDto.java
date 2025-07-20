package com.faiyaz.project.fittrack.dashboard.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponseDto {

    private int workoutsThisMonth;
    private int totalWorkouts;
    private double currentWeight;
    private LastWorkoutDto lastWorkout;
    private List<CalendarHeatmapDto> calendarHeatmap;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LastWorkoutDto {
        private LocalDate date;
        private String muscleGroup;
        private List<ExerciseSummary> exercises;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExerciseSummary {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CalendarHeatmapDto {
        private LocalDate date;
        private int count;
    }

}
