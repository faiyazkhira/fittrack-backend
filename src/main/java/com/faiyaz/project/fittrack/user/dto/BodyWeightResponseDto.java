package com.faiyaz.project.fittrack.user.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyWeightResponseDto {

    private UUID id;
    private double weight;
    private LocalDate dateLogged;
}
