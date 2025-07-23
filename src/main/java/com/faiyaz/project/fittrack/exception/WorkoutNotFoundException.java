package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class WorkoutNotFoundException extends FitTrackException {
    public WorkoutNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
