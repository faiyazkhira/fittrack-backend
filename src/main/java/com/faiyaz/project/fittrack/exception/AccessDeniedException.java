package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends FitTrackException {
    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
