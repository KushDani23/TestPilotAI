package com.testpilot.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ApiRequest DTO
 *
 * This class represents the data that the React frontend sends to the backend.
 * The user fills in these four fields in the UI form.
 *
 * @NotBlank ensures the field is not null, empty, or whitespace.
 * Spring's @Valid annotation on the controller triggers this validation automatically.
 */
public class ApiRequest {

    @NotBlank(message = "HTTP method is required (e.g., GET, POST, PUT, DELETE)")
    private String method;

    @NotBlank(message = "API endpoint is required (e.g., /users)")
    private String endpoint;

    @NotBlank(message = "API description is required")
    private String description;

    // Request body is optional — GET requests typically don't have one
    private String requestBody;

    // ── Constructors ──────────────────────────────────────────────────────────

    public ApiRequest() {}

    public ApiRequest(String method, String endpoint, String description, String requestBody) {
        this.method = method;
        this.endpoint = endpoint;
        this.description = description;
        this.requestBody = requestBody;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
