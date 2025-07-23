package com.faiyaz.project.fittrack.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends FitTrackException {
    public UserNotFoundException(String message) {
      super(message, HttpStatus.NOT_FOUND);
    }
}
