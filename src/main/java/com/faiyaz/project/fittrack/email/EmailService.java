package com.faiyaz.project.fittrack.email;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
