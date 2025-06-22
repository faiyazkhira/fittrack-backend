package com.faiyaz.project.fittrack.exception;

public class FitTrackException extends RuntimeException {
    public FitTrackException(String message) {
        super(message);
    }

    public FitTrackException(String message, Throwable cause){
        super(message, cause);
    }
}
