package com.testpilot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * TestCase DTO
 *
 * Represents a single test case (positive, negative, or validation).
 * Each test case has:
 * - title:          Short name of the test (e.g., "Valid User Registration")
 * - description:    What the test is checking
 * - expectedStatus: The HTTP status code expected (e.g., "200 OK", "400 Bad Request")
 * - requestBody:    The exact request body that triggers this specific test condition.
 *                   Typed as Object so Jackson can deserialize it from either a JSON
 *                   object ({"field":"value"}) or null from the AI response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCase {

    private String title;
    private String description;
    private String expectedStatus;

    // Per-test request body — each test case gets its own unique body
    // that exactly matches the scenario being tested.
    // Using Object so Jackson handles nested JSON objects from AI naturally.
    private Object requestBody;

    // ── Constructors ──────────────────────────────────────────────────────────

    public TestCase() {}

    public TestCase(String title, String description, String expectedStatus, Object requestBody) {
        this.title = title;
        this.description = description;
        this.expectedStatus = expectedStatus;
        this.requestBody = requestBody;
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

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }
}
