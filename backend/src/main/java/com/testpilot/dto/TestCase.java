package com.testpilot.dto;

/**
 * TestCase DTO
 *
 * Represents a single test case (positive, negative, or validation).
 * Each test case has:
 * - title: Short name of the test (e.g., "Valid User Registration")
 * - description: What the test is checking
 * - expectedStatus: The HTTP status code expected (e.g., "200 OK", "400 Bad Request")
 */
public class TestCase {

    private String title;
    private String description;
    private String expectedStatus;

    // ── Constructors ──────────────────────────────────────────────────────────

    public TestCase() {}

    public TestCase(String title, String description, String expectedStatus) {
        this.title = title;
        this.description = description;
        this.expectedStatus = expectedStatus;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectedStatus() {
        return expectedStatus;
    }

    public void setExpectedStatus(String expectedStatus) {
        this.expectedStatus = expectedStatus;
    }
}
