package com.faiyaz.project.fittrack.auth.service;

import com.faiyaz.project.fittrack.auth.jwt.JwtService;
import com.faiyaz.project.fittrack.user.dto.AuthResponse;
import com.faiyaz.project.fittrack.user.dto.LoginRequest;
import com.faiyaz.project.fittrack.user.dto.SignUpRequest;
import com.faiyaz.project.fittrack.user.entity.Role;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(SignUpRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    public AuthResponse login(LoginRequest request){
        UserDetails user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if(!user.isAccountNonLocked()){
            throw new LockedException("Account is locked. Please reset your password to unlock.");
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid Password");
        }
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }
}
