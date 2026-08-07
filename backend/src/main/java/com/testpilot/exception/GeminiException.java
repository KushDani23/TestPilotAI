package com.testpilot.exception;

/**
 * GeminiException
 *
 * A custom runtime exception thrown when there is an error communicating
 * with the Google Gemini API or when the response cannot be parsed.
 *
 * Using a custom exception (instead of a generic RuntimeException) makes
 * error handling more explicit and allows the GlobalExceptionHandler to
 * catch it specifically and return a meaningful error message to the user.
 */
public class GeminiException extends RuntimeException {

    public GeminiException(String message) {
        super(message);
    }

    public GeminiException(String message, Throwable cause) {
        super(message, cause);
    }
}
