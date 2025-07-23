package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends FitTrackException {
    public AuthenticationFailedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
