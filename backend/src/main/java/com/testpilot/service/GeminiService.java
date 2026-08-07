package com.testpilot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpilot.exception.GeminiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * GeminiService (now powered by Groq)
 *
 * This service handles all communication with the Groq API.
 * Groq uses an OpenAI-compatible REST API, making it easy to integrate.
 *
 * Groq API request structure:
 * POST https://api.groq.com/openai/v1/chat/completions
 * Headers: Authorization: Bearer <key>, Content-Type: application/json
 * Body:
 * {
 *   "model": "llama3-8b-8192",
 *   "messages": [{ "role": "user", "content": "..." }],
 *   "temperature": 0.7,
 *   "max_tokens": 2048
 * }
 *
 * Groq API response structure:
 * {
 *   "choices": [
 *     {
 *       "message": {
 *         "content": "...actual response here..."
 *       }
 *     }
 *   ]
 * }
 *
 * We navigate this structure using Jackson's JsonNode to extract the "content" field.
 */
@Service
public class GeminiService {

    // Read Groq API key from application.properties
    @Value("${groq.api.key}")
    private String groqApiKey;

    // Read Groq API URL from application.properties
    @Value("${groq.api.url}")
    private String groqApiUrl;

    // Read the model name from application.properties
    @Value("${groq.model}")
    private String groqModel;

    // RestTemplate is Spring's HTTP client for making REST calls
    private final RestTemplate restTemplate = new RestTemplate();

    // ObjectMapper is Jackson's tool for parsing JSON strings
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends the given prompt to the Groq API and returns the raw text response.
     *
     * @param prompt - The full prompt string to send to Groq
     * @return Raw text response from Groq (expected to be JSON)
     * @throws GeminiException if the API call fails or the response is malformed
     */
    public String generateContent(String prompt) {

        // Build request body using OpenAI-compatible chat completions format
        Map<String, Object> requestBody = Map.of(
            "model", groqModel,
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.7,
            "max_tokens", 2048
        );

        // Set HTTP headers — Bearer token auth for Groq
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Make the POST request to Groq API
            ResponseEntity<String> response = restTemplate.exchange(
                groqApiUrl,
                HttpMethod.POST,
                httpEntity,
                String.class
            );

            // Parse the response and extract the text content
            return extractTextFromGroqResponse(response.getBody());

        } catch (Exception ex) {
            throw new GeminiException(
                "Failed to communicate with Groq API. Please check your API key and try again. Details: " + ex.getMessage(),
                ex
            );
        }
    }

    /**
     * Navigates Groq's response JSON to extract the actual text content.
     *
     * Response path: choices[0] → message → content
     */
    private String extractTextFromGroqResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode contentNode = root
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

            if (contentNode.isMissingNode() || contentNode.isNull()) {
                throw new GeminiException("Groq returned an empty response. Please try again.");
            }

            return contentNode.asText();

        } catch (GeminiException ex) {
            throw ex; // Re-throw our custom exception as-is
        } catch (Exception ex) {
            throw new GeminiException("Failed to parse Groq API response.", ex);
        }
    }
}
