package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends FitTrackException {
    public EmailAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
