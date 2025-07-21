package com.faiyaz.project.fittrack.auth.controller;

import com.faiyaz.project.fittrack.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private String frontendUrl = "http://dummyTestURL";

    private final EmailService emailService;

    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<String> secured(){
        return ResponseEntity.ok("You're inside a protected route");
    }

    @GetMapping("/send-email")
    public ResponseEntity<String> sendTestEmail(){

        String token = UUID.randomUUID().toString();
        String user = "Mickey mouse";
        String url = frontendUrl + "/reset-password?token=" + token;
        String body = String.format(
                "Hello %s,\n\n" +
                        "We received a request to reset your password. Click the link below to choose a new password:\n\n" +
                        "%s\n\n" +
                        "This link will expire in 15 minutes.\n\n" +
                        "Best regards,\n" +
                        "Repwise Team",
                user, url
        );

        emailService.sendEmail("khirafaiyaz@gmail.com", "Password Reset Request", body);
        return ResponseEntity.ok("Email sent");
    }
}
