package com.faiyaz.project.fittrack.config;

import com.faiyaz.project.fittrack.exercise.entity.ExerciseCatalog;
import com.faiyaz.project.fittrack.exercise.entity.MuscleGroup;
import com.faiyaz.project.fittrack.exercise.repository.ExerciseCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final ExerciseCatalogRepository catalogRepository;

    @Bean
    public CommandLineRunner seedExerciseCatalog(){
        return args -> {
            if (catalogRepository.count() == 0) {
                List<ExerciseCatalog> catalogEntries = List.of(
                        // Chest
                        new ExerciseCatalog(null, "Barbell Bench Press", MuscleGroup.CHEST, true),
                        new ExerciseCatalog(null, "Incline Dumbbell Press", MuscleGroup.CHEST, true),
                        new ExerciseCatalog(null, "Chest Fly Machine", MuscleGroup.CHEST, true),
                        new ExerciseCatalog(null, "Push Ups", MuscleGroup.CHEST, true),

                        // Back
                        new ExerciseCatalog(null, "Pull Ups", MuscleGroup.BACK, true),
                        new ExerciseCatalog(null, "Lat Pulldown", MuscleGroup.BACK, true),
                        new ExerciseCatalog(null, "Barbell Rows", MuscleGroup.BACK, true),
                        new ExerciseCatalog(null, "Seated Cable Rows", MuscleGroup.BACK, true),

                        // Shoulders
                        new ExerciseCatalog(null, "Overhead Press", MuscleGroup.SHOULDERS, true),
                        new ExerciseCatalog(null, "Lateral Raises", MuscleGroup.SHOULDERS, true),
                        new ExerciseCatalog(null, "Front Raises", MuscleGroup.SHOULDERS, true),
                        new ExerciseCatalog(null, "Reverse Pec Deck", MuscleGroup.SHOULDERS, true),

                        // Arms
                        new ExerciseCatalog(null, "Barbell Bicep Curl", MuscleGroup.BICEPS, true),
                        new ExerciseCatalog(null, "Dumbbell Hammer Curl", MuscleGroup.BICEPS, true),
                        new ExerciseCatalog(null, "Tricep Pushdown", MuscleGroup.TRICEPS, true),
                        new ExerciseCatalog(null, "Skull Crushers", MuscleGroup.TRICEPS, true),

                        // Legs
                        new ExerciseCatalog(null, "Barbell Squat", MuscleGroup.LEGS, true),
                        new ExerciseCatalog(null, "Leg Press", MuscleGroup.LEGS, true),
                        new ExerciseCatalog(null, "Romanian Deadlift", MuscleGroup.LEGS, true),
                        new ExerciseCatalog(null, "Lunges", MuscleGroup.LEGS, true),
                        new ExerciseCatalog(null, "Leg Curl Machine", MuscleGroup.LEGS, true),
                        new ExerciseCatalog(null, "Leg Extension Machine", MuscleGroup.LEGS, true),

                        // Core
                        new ExerciseCatalog(null, "Plank", MuscleGroup.CORE, true),
                        new ExerciseCatalog(null, "Hanging Leg Raises", MuscleGroup.CORE, true),
                        new ExerciseCatalog(null, "Cable Crunch", MuscleGroup.CORE, true),
                        new ExerciseCatalog(null, "Russian Twists", MuscleGroup.CORE, true)
                );

                catalogRepository.saveAll(catalogEntries);
                System.out.println("Seeded global ExerciseCatalog.");
            }
        };
    }
}
