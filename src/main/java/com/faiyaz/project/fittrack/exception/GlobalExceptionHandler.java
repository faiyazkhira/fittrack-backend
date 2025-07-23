package com.faiyaz.project.fittrack.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Getter
    public static class ErrorResponse {
        private final int status;
        private final String error;
        private final String message;
        private final String path;
        private final String timestamp;

        public ErrorResponse(HttpStatus status, String message, String path){
            this.status = status.value();
            this.error = status.getReasonPhrase();
            this.message = message;
            this.path = path;
            this.timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request){
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status, message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request){
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleBadRequest(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed or invalid input", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request){
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Access denied", request);
    }

    @ExceptionHandler(FitTrackException.class)
    public ResponseEntity<?> handleGeneric(FitTrackException ex, HttpServletRequest request){
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        logger.warn("Handled FitTrackException: {}", ex.getMessage());
        return buildErrorResponse(status, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherExceptions(Exception ex, HttpServletRequest request){
        logger.error("Unhandled exception occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred. Please contact support.", request);
    }
}
