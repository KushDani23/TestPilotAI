package com.testpilot.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpilot.dto.ApiRequest;
import com.testpilot.dto.TestCaseResponse;
import com.testpilot.exception.LLMException;
import org.springframework.stereotype.Service;

@Service
public class TestCaseService {

    private final LLMService llmService;

    private final ObjectMapper objectMapper;

    public TestCaseService(LLMService llmService) {
        this.llmService = llmService;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public TestCaseResponse generateTestCases(ApiRequest request, String groqApiKey) {
        String prompt = buildPrompt(request);
        String rawResponse = llmService.generateContent(prompt, groqApiKey);
        return parseResponse(rawResponse);
    }

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
                """
                .formatted(
                        request.getMethod().toUpperCase(),
                        request.getEndpoint(),
                        request.getDescription(),
                        sampleBody);
    }

    private TestCaseResponse parseResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return buildFallbackResponse("AI returned an empty response. Please try again.");
        }

        String cleaned = rawResponse.trim();

        // Step 1 — strip markdown fences (handles ```json, ```JSON, ``` etc.)
        cleaned = cleaned.replaceAll("(?s)^```[a-zA-Z]*\\s*", "").replaceAll("(?s)\\s*```$", "").trim();

        // Step 2 — locate start of JSON
        int firstBrace = cleaned.indexOf('{');
        if (firstBrace >= 0) {
            cleaned = cleaned.substring(firstBrace);
        }

        // Step 3 — pre-sanitize common LLM formatting slips:
        // a) Stray quotes before object braces: ,"{" -> ,{ or ["{" -> [{
        cleaned = cleaned.replaceAll(",\\s*\"\\{", ",{")
                .replaceAll("\\[\\s*\"\\{", "[{");
        // b) Missing opening brace before keys: },"title": -> },{"title":
        cleaned = cleaned.replaceAll("\\}\\s*,\\s*\"title\"", "},{\"title\"")
                .replaceAll("\\}\\s*,\\s*\"status\"", "},{\"status\"");

        // Step 4 — parse with streaming JsonParser: automatically stops at matching
        // closing brace,
        // safely ignoring trailing content or accidental extra closing braces
        try (JsonParser parser = objectMapper.createParser(cleaned)) {
            TestCaseResponse response = parser.readValueAs(TestCaseResponse.class);
            if (response != null && response.getSummary() != null) {
                return response;
            }
        } catch (Exception firstAttempt) {
            // Step 5 — fallback substring attempt
            try {
                int start = cleaned.indexOf('{');
                int end = cleaned.lastIndexOf('}');
                if (start >= 0 && end > start) {
                    return objectMapper.readValue(cleaned.substring(start, end + 1), TestCaseResponse.class);
                }
            } catch (Exception ignored) {
            }

            // If all parsing attempts fail, return a readable fallback
            return buildFallbackResponse(
                    "The AI returned an unexpected format. Shown below is the raw output:\n\n" + rawResponse);
        }

        return buildFallbackResponse(
                "The AI returned an unexpected format. Shown below is the raw output:\n\n" + rawResponse);
    }

    // Returns a structured fallback TestCaseResponse when parsing completely fails
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
