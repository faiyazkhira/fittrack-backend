package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class ExerciseSetNotFoundException extends FitTrackException {
    public ExerciseSetNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
