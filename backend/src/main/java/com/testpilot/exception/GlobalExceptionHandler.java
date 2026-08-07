package com.testpilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler
 *
 * This class handles all exceptions thrown anywhere in the application.
 * Instead of each controller method needing its own try-catch blocks,
 * we centralize error handling here.
 *
 * @RestControllerAdvice tells Spring to apply this handler across all controllers.
 * @ExceptionHandler marks each method to handle a specific exception type.
 *
 * All error responses return a structured JSON body with a "error" key,
 * making it easy for the React frontend to display the error message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors triggered by @Valid annotation.
     * For example, when the user submits the form with an empty "method" field,
     * Spring throws MethodArgumentNotValidException.
     * We collect all field errors and return them as a map.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Loop through all field-level validation failures and collect them
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles our custom GeminiException.
     * Thrown when Gemini API call fails or returns an unparseable response.
     */
    @ExceptionHandler(GeminiException.class)
    public ResponseEntity<Map<String, String>> handleGeminiException(GeminiException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    /**
     * Catches any unexpected exception that isn't handled above.
     * Acts as a safety net — the user always sees a clean error message
     * instead of a raw Java stack trace.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred. Please try again.");
        // Log the actual exception for debugging (in a real app, use a logger)
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
