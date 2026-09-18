package com.testpilot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseResponse {

    private String summary;
    private List<TestCase> positiveTests;
    private List<TestCase> negativeTests;
    private List<TestCase> validationTests;
    private List<ExpectedResponse> expectedResponses;

    public TestCaseResponse() {}

    public TestCaseResponse(String summary,
                            List<TestCase> positiveTests,
                            List<TestCase> negativeTests,
                            List<TestCase> validationTests,
                            List<ExpectedResponse> expectedResponses) {
        this.summary = summary;
        this.positiveTests = positiveTests;
        this.negativeTests = negativeTests;
        this.validationTests = validationTests;
        this.expectedResponses = expectedResponses;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<TestCase> getPositiveTests() {
        return positiveTests;
    }

    public void setPositiveTests(List<TestCase> positiveTests) {
        this.positiveTests = positiveTests;
    }

    public List<TestCase> getNegativeTests() {
        return negativeTests;
    }

    public void setNegativeTests(List<TestCase> negativeTests) {
        this.negativeTests = negativeTests;
    }

    public List<TestCase> getValidationTests() {
        return validationTests;
    }

    public void setValidationTests(List<TestCase> validationTests) {
        this.validationTests = validationTests;
    }

    public List<ExpectedResponse> getExpectedResponses() {
        return expectedResponses;
    }

    public void setExpectedResponses(List<ExpectedResponse> expectedResponses) {
        this.expectedResponses = expectedResponses;
    }
}
