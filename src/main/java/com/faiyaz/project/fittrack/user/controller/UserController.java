package com.faiyaz.project.fittrack.user.controller;

import com.faiyaz.project.fittrack.user.dto.UserProfileRequestDto;
import com.faiyaz.project.fittrack.user.dto.UserProfileResponseDto;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getUser(@AuthenticationPrincipal UserDetails userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setGender(user.getGender());

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody UserProfileRequestDto request){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setGender(request.getGender());

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated");
    }
}
