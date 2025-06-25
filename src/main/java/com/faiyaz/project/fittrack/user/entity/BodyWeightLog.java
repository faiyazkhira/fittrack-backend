package com.faiyaz.project.fittrack.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "body_weight_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BodyWeightLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private LocalDate dateLogged;
}
