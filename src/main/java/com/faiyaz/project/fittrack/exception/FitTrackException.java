package com.faiyaz.project.fittrack.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FitTrackException extends RuntimeException {
    private final HttpStatus status;

    public FitTrackException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }


}
