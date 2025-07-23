package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class InvalidExerciseInputException extends FitTrackException {
    public InvalidExerciseInputException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
