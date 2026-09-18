package com.testpilot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCase {

    private String title;
    private String description;
    private String expectedStatus;

    private Object requestBody;

    public TestCase() {}

    public TestCase(String title, String description, String expectedStatus, Object requestBody) {
        this.title = title;
        this.description = description;
        this.expectedStatus = expectedStatus;
        this.requestBody = requestBody;
    }

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
