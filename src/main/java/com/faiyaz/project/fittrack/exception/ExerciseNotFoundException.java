package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class ExerciseNotFoundException extends FitTrackException {
    public ExerciseNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
