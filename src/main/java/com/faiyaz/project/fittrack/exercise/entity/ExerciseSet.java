package com.faiyaz.project.fittrack.exercise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exercise_sets")
public class ExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private int reps;

    @Column(nullable = false)
    private double weight;

    @Column(length = 255)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime loggedAt;

    @PrePersist
    public void prePersist(){
        if(this.loggedAt == null){
            this.loggedAt = LocalDateTime.now();
        }
    }
}
