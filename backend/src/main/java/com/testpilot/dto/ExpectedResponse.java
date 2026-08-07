package com.testpilot.dto;

/**
 * ExpectedResponse DTO
 *
 * Represents an expected HTTP response entry.
 * Each entry maps an HTTP status code to a human-readable message.
 *
 * Example:
 *   status: "201 Created"
 *   message: "User successfully registered and returned in response body"
 */
public class ExpectedResponse {

    private String status;
    private String message;

    // ── Constructors ──────────────────────────────────────────────────────────

    public ExpectedResponse() {}

    public ExpectedResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
