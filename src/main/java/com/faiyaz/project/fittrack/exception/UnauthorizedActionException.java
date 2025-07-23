package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends FitTrackException{
    public UnauthorizedActionException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
