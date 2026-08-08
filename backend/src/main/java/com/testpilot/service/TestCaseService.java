package com.testpilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpilot.dto.ApiRequest;
import com.testpilot.dto.TestCaseResponse;
import com.testpilot.exception.GeminiException;
import org.springframework.stereotype.Service;

/**
 * TestCaseService
 *
 * This is the main business logic layer.
 * It sits between the Controller and the GeminiService.
 *
 * Responsibilities:
 * 1. Build the prompt using the user's API details
 * 2. Call GeminiService to get the AI-generated response
 * 3. Parse the JSON response into a TestCaseResponse DTO
 * 4. Return the structured DTO to the Controller
 *
 * The Controller doesn't know anything about Gemini or JSON parsing —
 * that's all handled here. This is the "Service Layer" in layered architecture.
 */
@Service
public class TestCaseService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    // Spring automatically injects GeminiService via constructor injection
    public TestCaseService(GeminiService geminiService) {
        this.geminiService = geminiService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Orchestrates the full test case generation process.
     *
     * @param request - DTO containing method, endpoint, description, requestBody
     * @return TestCaseResponse DTO containing all generated test cases
     */
    public TestCaseResponse generateTestCases(ApiRequest request) {
        // Step 1: Build the prompt
        String prompt = buildPrompt(request);

        // Step 2: Send prompt to Gemini and get the raw text back
        String rawResponse = geminiService.generateContent(prompt);

        // Step 3: Parse the raw JSON text into our DTO
        return parseGeminiResponse(rawResponse);
    }

    /**
     * Builds the prompt to send to Gemini.
     *
     * The prompt follows the role-based instruction pattern:
     * - We tell Gemini to act as a Senior QA Engineer
     * - We inject the user's API details
     * - We tell it exactly what format to return (JSON)
     *
     * This is "prompt engineering" — structuring the prompt to get reliable output.
     */
    private String buildPrompt(ApiRequest request) {
        String requestBodySection = (request.getRequestBody() != null && !request.getRequestBody().isBlank())
            ? request.getRequestBody()
            : "No request body (e.g., GET request)";

        return """
            You are a Senior QA Engineer.
            Analyze the following REST API and generate comprehensive test cases.

            HTTP Method: %s
            Endpoint: %s
            Description: %s
            Sample Request Body: %s

            Generate the following:
            1. API Summary
            2. Positive Test Cases (valid inputs, happy path)
            3. Negative Test Cases (invalid inputs, wrong types, error scenarios)
            4. Validation Test Cases (boundary values, empty fields, missing fields, null)
            5. Expected HTTP Status Codes and Response Messages

            STRICT RULES — follow all of these exactly:
            - Return ONLY valid JSON. No explanation, no markdown, no code fences.
            - Generate 4 to 6 test cases for each category.
            - Every single test case MUST include a "requestBody" field.
            - Each "requestBody" MUST be unique and exactly match what that specific test scenario requires:
                * Positive tests: use complete, valid field values (realistic names, valid emails like john.doe@example.com).
                * Negative tests: use wrong types, invalid email formats, unauthorized values, or completely wrong data.
                * Validation tests: use boundary conditions — empty string "", null, missing fields (omit the key), a string of exactly 1 character, a string of exactly 255 characters, a string of exactly 256 characters (to test max-length boundary), or strings containing only spaces.
            - Do NOT use the same "requestBody" object for more than one test case.
            - Email values must be plain strings like john.doe@example.com — no Markdown, no angle brackets, no links.
            - Do not generate code samples.

            Return the response in this exact JSON format:
            {
              "summary": "",
              "positiveTests": [
                { "title": "", "description": "", "expectedStatus": "", "requestBody": {} }
              ],
              "negativeTests": [
                { "title": "", "description": "", "expectedStatus": "", "requestBody": {} }
              ],
              "validationTests": [
                { "title": "", "description": "", "expectedStatus": "", "requestBody": {} }
              ],
              "expectedResponses": [
                { "status": "", "message": "" }
              ]
            }
            """.formatted(
                request.getMethod().toUpperCase(),
                request.getEndpoint(),
                request.getDescription(),
                requestBodySection
            );
    }

    /**
     * Parses the raw JSON string returned by Gemini into a TestCaseResponse DTO.
     *
     * Gemini is instructed to return only JSON, but sometimes it wraps the response
     * in markdown code fences (```json ... ```). We clean that up before parsing.
     *
     * Jackson's ObjectMapper converts the JSON string directly into our Java DTO class.
     */
    private TestCaseResponse parseGeminiResponse(String rawResponse) {
        try {
            // Remove markdown code fences if Gemini accidentally adds them
            String cleaned = rawResponse.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("^```(json)?\\s*", "").replaceAll("```\\s*$", "").trim();
            }

            // Use Jackson to deserialize the JSON string into our TestCaseResponse DTO
            return objectMapper.readValue(cleaned, TestCaseResponse.class);

        } catch (Exception ex) {
            throw new GeminiException(
                "Failed to parse AI response. The model may have returned an unexpected format. Please try again.",
                ex
            );
        }
    }
}
