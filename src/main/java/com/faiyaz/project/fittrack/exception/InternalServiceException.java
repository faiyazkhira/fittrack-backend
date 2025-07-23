package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class InternalServiceException extends FitTrackException {
    public InternalServiceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
