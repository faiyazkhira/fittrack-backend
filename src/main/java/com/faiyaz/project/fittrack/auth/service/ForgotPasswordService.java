package com.faiyaz.project.fittrack.auth.service;

import com.faiyaz.project.fittrack.auth.entity.PasswordResetToken;
import com.faiyaz.project.fittrack.auth.entity.SecureAccountToken;
import com.faiyaz.project.fittrack.auth.repository.PasswordResetTokenRepository;
import com.faiyaz.project.fittrack.auth.repository.SecureAccountTokenRepository;
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
    private final SecureAccountTokenRepository secureAccountRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public ForgotPasswordService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, PasswordEncoder passwordEncoder, EmailService emailService, SecureAccountTokenRepository secureAccountRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.secureAccountRepository = secureAccountRepository;
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

            String resetPasswordUrl = frontendUrl + "/reset-password?token=" + newToken;
            String body = String.format(
                    "Hello %s,\n\n" +
                            "Your previous password reset link expired. We've generated a new one for you:\n\n" +
                            "%s\n\n" +
                            "This link will expire in 15 minutes.\n\n" +
                            "Best regards,\n" +
                            "Repwise Team",
                    user.getName(), resetPasswordUrl
            );
            emailService.sendEmail(user.getEmail(), "New Password Reset Link", body);

            return "Your token was expired. We've sent you a new link";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setAccountLocked(false);
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        String secureToken = UUID.randomUUID().toString();
        SecureAccountToken secureAccountToken = SecureAccountToken.builder()
                .token(secureToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        secureAccountRepository.save(secureAccountToken);

        String secureAccountUrl = frontendUrl + "/secure-account?token=" + secureToken;

        String body = String.format(
                "Hello %s,\n\n" +
                        "Your password has been successfully reset. If this wasn't you, please secure your account immediately:\n\n%s\n\nThis will lock your account and require a new password reset.\n\n" +
                        "Best regards,\n" +
                        "Repwise Team",
                user.getName(), secureAccountUrl
        );

        emailService.sendEmail(user.getEmail(), "Your Password Has Been Reset", body);

        return "Password reset successful. Secure account link has been shared via email";
    }
}
