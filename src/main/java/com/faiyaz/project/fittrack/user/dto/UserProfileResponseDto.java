package com.faiyaz.project.fittrack.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDto {

    private String email;
    private String name;
    private int age;
    private Double height;
    private Double weight;
    private String gender;
}
