package com.faiyaz.project.fittrack.user.service;

import com.faiyaz.project.fittrack.user.dto.UserProfileRequestDto;
import com.faiyaz.project.fittrack.user.dto.UserProfileResponseDto;
import com.faiyaz.project.fittrack.user.entity.BodyWeightLog;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.BodyWeightRepository;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BodyWeightRepository bodyWeightRepository;

    public UserService(UserRepository userRepository, BodyWeightRepository bodyWeightRepository) {
        this.userRepository = userRepository;
        this.bodyWeightRepository = bodyWeightRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserProfileResponseDto getUser(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileResponseDto dto = new UserProfileResponseDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setGender(user.getGender());

        return dto;
    }

    public void updateUser(UserDetails userDetails, @Valid UserProfileRequestDto request) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setGender(request.getGender());
        userRepository.save(user);

        if(request.getWeight() != null){
            BodyWeightLog initialLog = BodyWeightLog.builder()
                    .dateLogged(LocalDate.now())
                    .user(user)
                    .weight(request.getWeight())
                    .build();

            bodyWeightRepository.save(initialLog);
        }
    }
}
