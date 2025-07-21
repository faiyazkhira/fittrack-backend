package com.faiyaz.project.fittrack.auth.service;

import com.faiyaz.project.fittrack.auth.entity.PasswordResetToken;
import com.faiyaz.project.fittrack.auth.entity.SecureAccountToken;
import com.faiyaz.project.fittrack.auth.repository.PasswordResetTokenRepository;
import com.faiyaz.project.fittrack.auth.repository.SecureAccountTokenRepository;
import com.faiyaz.project.fittrack.email.EmailService;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SecureAccountService {

    private final UserRepository userRepository;
    private final SecureAccountTokenRepository secureTokenRepository;
    private final EmailService emailService;
    private final PasswordResetTokenRepository resetTokenRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public SecureAccountService(UserRepository userRepository, SecureAccountTokenRepository secureTokenRepository, EmailService emailService, PasswordResetTokenRepository resetTokenRepository) {
        this.userRepository = userRepository;
        this.secureTokenRepository = secureTokenRepository;
        this.emailService = emailService;
        this.resetTokenRepository = resetTokenRepository;
    }


    public void secureAccount(String token){
        SecureAccountToken secureToken = secureTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if(secureToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Token expired");
        }

        User user = secureToken.getUser();
        user.setAccountLocked(true);
        userRepository.save(user);
        secureTokenRepository.delete(secureToken);

        //Force reset flow: send reset email
        String resetToken = UUID.randomUUID().toString();
        PasswordResetToken newResetToken = PasswordResetToken.builder()
                .token(resetToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        resetTokenRepository.save(newResetToken);

        String url = frontendUrl + "/reset-password?token=" + resetToken;
        String body = String.format(
                "Hello %s,\n\nWe've secured your account. To regain access, please reset your password:\n\n%s\n\nThis link will expire in 15 minutes.\n\nBest,\nRepwise Team",
                user.getName(), url
        );
        emailService.sendEmail(user.getEmail(), "Reset Your Password to Unlock Your Account", body);
    }
}
