package com.faiyaz.project.fittrack.user.controller;

import com.faiyaz.project.fittrack.user.dto.UserProfileRequestDto;
import com.faiyaz.project.fittrack.user.dto.UserProfileResponseDto;
import com.faiyaz.project.fittrack.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getUser(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(userService.getUser(userDetails));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @Valid @RequestBody UserProfileRequestDto request){
        userService.updateUser(userDetails, request);
        return ResponseEntity.ok("Profile updated");
    }
}
