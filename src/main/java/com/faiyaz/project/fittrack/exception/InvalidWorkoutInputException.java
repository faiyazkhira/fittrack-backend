package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class InvalidWorkoutInputException extends FitTrackException {
    public InvalidWorkoutInputException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
