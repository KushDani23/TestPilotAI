package com.testpilot.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpilot.dto.ApiRequest;
import com.testpilot.dto.TestCaseResponse;
import com.testpilot.exception.GeminiException;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TestCaseService
 *
 * Business logic layer: builds the Groq prompt, calls GeminiService,
 * and parses the AI response into a TestCaseResponse DTO.
 */
@Service
public class TestCaseService {

    private final GeminiService geminiService;

    // Lenient ObjectMapper — ignores unknown fields so new AI output fields
    // never break parsing, and doesn't fail on nulls or empty values.
    private final ObjectMapper objectMapper;

    public TestCaseService(GeminiService geminiService) {
        this.geminiService = geminiService;
        this.objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    /**
     * Main entry point — orchestrates prompt → AI call → parse → return.
     */
    public TestCaseResponse generateTestCases(ApiRequest request) {
        String prompt      = buildPrompt(request);
        String rawResponse = geminiService.generateContent(prompt);
        return parseResponse(rawResponse);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Prompt Builder
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Builds the structured Groq prompt.
     *
     * Key design decisions:
     * - requestBody inside each test case is a JSON OBJECT (not a string), so
     *   the download file has nicely structured data.
     * - Boundary/length tests use SHORT representative values (e.g. "a" for 1-char,
     *   "invalid-email" for bad email) — NOT 255-char strings, because very long
     *   strings inside JSON often cause models to produce malformed output.
     * - Prompt is kept concise to reduce hallucination.
     */
    private String buildPrompt(ApiRequest request) {
        String sampleBody = (request.getRequestBody() != null && !request.getRequestBody().isBlank())
            ? request.getRequestBody()
            : "{}";

        return """
            You are a Senior QA Engineer. Generate test cases for the REST API below.

            HTTP Method : %s
            Endpoint    : %s
            Description : %s
            Sample Body : %s

            Output ONLY a single, valid JSON object — no markdown, no code fences, no extra text.

            Rules:
            1. Each test case must have: "title", "description", "expectedStatus", "requestBody".
            2. "requestBody" must be a JSON object unique to that test case — matching exactly what that scenario tests:
               - Positive  → complete valid data (real names, valid emails like alice@example.com).
               - Negative  → invalid values (wrong types, bad email like "not-an-email", numeric where string needed).
               - Validation→ edge cases: omit required fields, use empty string "", use null, use " " (spaces only).
            3. Do NOT repeat the same requestBody in two test cases.
            4. Generate 4 test cases per category (positive, negative, validation).
            5. expectedStatus must be a short string like "200 OK" or "400 Bad Request".

            JSON format to follow exactly:
            {
              "summary": "string",
              "positiveTests": [
                { "title": "string", "description": "string", "expectedStatus": "string", "requestBody": {} }
              ],
              "negativeTests": [
                { "title": "string", "description": "string", "expectedStatus": "string", "requestBody": {} }
              ],
              "validationTests": [
                { "title": "string", "description": "string", "expectedStatus": "string", "requestBody": {} }
              ],
              "expectedResponses": [
                { "status": "string", "message": "string" }
              ]
            }
            """.formatted(
                request.getMethod().toUpperCase(),
                request.getEndpoint(),
                request.getDescription(),
                sampleBody
            );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Response Parser — bulletproof, never throws a user-facing error
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Extracts and parses the JSON block from the AI's raw response.
     *
     * Three-step cleaning strategy:
     * 1. Strip any markdown code fences (```json ... ``` or ``` ... ```).
     * 2. Extract the outermost { ... } block using regex — handles cases where
     *    the model prepends/appends text around the JSON.
     * 3. Parse with a lenient ObjectMapper (ignores unknown fields).
     *
     * If parsing still fails, a structured fallback response is returned so the
     * user always sees something useful instead of an error page.
     */
    private TestCaseResponse parseResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return buildFallbackResponse("AI returned an empty response. Please try again.");
        }

        String cleaned = rawResponse.trim();

        // Step 1 — strip markdown fences (handles ```json, ```JSON, ``` etc.)
        cleaned = cleaned.replaceAll("(?s)^```[a-zA-Z]*\\s*", "").replaceAll("(?s)\\s*```$", "").trim();

        // Step 2 — extract the outermost JSON object using regex
        // This handles cases where the model adds text before or after the JSON
        Pattern jsonPattern = Pattern.compile("(?s)\\{.*\\}");
        Matcher matcher     = jsonPattern.matcher(cleaned);
        if (matcher.find()) {
            cleaned = matcher.group();
        }

        // Step 3 — parse with lenient ObjectMapper
        try {
            return objectMapper.readValue(cleaned, TestCaseResponse.class);
        } catch (Exception firstAttempt) {
            // Step 4 — last resort: try to extract by finding first { and last }
            try {
                int start = cleaned.indexOf('{');
                int end   = cleaned.lastIndexOf('}');
                if (start >= 0 && end > start) {
                    String extracted = cleaned.substring(start, end + 1);
                    return objectMapper.readValue(extracted, TestCaseResponse.class);
                }
            } catch (Exception ignored) {
                // Fall through to fallback
            }

            // If all parsing attempts fail, return a readable fallback
            // so the user sees a result instead of a red error box
            return buildFallbackResponse(
                "The AI returned an unexpected format. Shown below is the raw output:\n\n" + rawResponse
            );
        }
    }

    /**
     * Returns a structured fallback TestCaseResponse when parsing completely fails.
     * This ensures the frontend always renders something instead of crashing.
     */
    private TestCaseResponse buildFallbackResponse(String message) {
        TestCaseResponse fallback = new TestCaseResponse();
        fallback.setSummary(message);
        fallback.setPositiveTests(java.util.Collections.emptyList());
        fallback.setNegativeTests(java.util.Collections.emptyList());
        fallback.setValidationTests(java.util.Collections.emptyList());
        fallback.setExpectedResponses(java.util.Collections.emptyList());
        return fallback;
    }
}
