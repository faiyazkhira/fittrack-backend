package com.faiyaz.project.fittrack.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BodyWeightRequestDto {

    @NotNull(message = "Weight must be provided")
    private Double weight;

    private LocalDate dateLogged;
}
