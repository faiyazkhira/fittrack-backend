package com.faiyaz.project.fittrack.auth.service;

import com.faiyaz.project.fittrack.auth.entity.PasswordResetToken;
import com.faiyaz.project.fittrack.auth.repository.PasswordResetTokenRepository;
import com.faiyaz.project.fittrack.email.EmailService;
import com.faiyaz.project.fittrack.user.entity.AuthProvider;
import com.faiyaz.project.fittrack.user.entity.User;
import com.faiyaz.project.fittrack.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public ForgotPasswordService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void processForgotPassword(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(user.getAuthProvider() != AuthProvider.LOCAL){
            throw new IllegalArgumentException("Password reset not supported for: " + user.getAuthProvider());
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);

        String url = frontendUrl + "/reset-password?token=" + token;
        String body = String.format(
                "Hello %s,\n\n" +
                        "We received a request to reset your password. Click the link below to choose a new password:\n\n" +
                        "%s\n\n" +
                        "This link will expire in 15 minutes.\n\n" +
                        "Best regards,\n" +
                        "Repwise Team",
                user.getName(), url
        );
        emailService.sendEmail(user.getEmail(), "Password Reset Request", body);
    }

    public String resetPassword(String token, String newPassword){
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            User user = resetToken.getUser();
            tokenRepository.delete(resetToken);

            String newToken = UUID.randomUUID().toString();
            PasswordResetToken newResetToken = PasswordResetToken.builder()
                    .token(newToken)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusMinutes(15))
                    .build();
            tokenRepository.save(newResetToken);

            String url = frontendUrl + "/reset-password?token=" + newToken;
            String body = String.format(
                    "Hello %s,\n\n" +
                            "Your previous password reset link expired. We've generated a new one for you:\n\n" +
                            "%s\n\n" +
                            "This link will expire in 15 minutes.\n\n" +
                            "Best regards,\n" +
                            "Repwise Team",
                    user.getName(), url
            );
            emailService.sendEmail(user.getEmail(), "New Password Reset Link", body);

            return "Your token was expired. We've sent you a new link";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        String body = String.format(
                "Hello %s,\n\n" +
                        "Your password has been successfully reset. If you didn't perform this action, please contact our support team immediately.\n\n" +
                        "Best regards,\n" +
                        "Repwise Team",
                user.getName()
        );
        emailService.sendEmail(user.getEmail(), "Your Password Has Been Reset", body);

        return "Password reset successful.";
    }
}
