package com.faiyaz.project.fittrack.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequestDto {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @Min(value = 10, message = "Age must be at least 10.")
    @Max(value = 100, message = "Age must be less than or equal to 100.")
    private int age;

    @DecimalMin(value = "100.0", message = "Height must be at least 100 cm.")
    @DecimalMax(value = "250.0", message = "Height must be less than or equal to 250 cm.")
    private Double height;

    @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg.")
    @DecimalMax(value = "300.0", message = "Weight must be less than or equal to 300 kg.")
    private Double weight;

    @Pattern(
            regexp = "^(male|female|other)$",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Gender must be male, female, or other."
    )
    private String gender;


}
