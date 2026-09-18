package com.testpilot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testpilot.exception.LLMException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LLMService {

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Calls the Groq API to generate LLM content.
     *
     * @param prompt     The engineered prompt to send to the model.
     * @param groqApiKey The user-supplied Groq API key forwarded from the request header.
     * @return The raw text content returned by the LLM.
     */
    public String generateContent(String prompt, String groqApiKey) {

        Map<String, Object> requestBody = Map.of(
            "model", groqModel,
            "messages", List.of(
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 0.7,
            "max_tokens", 2048
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiKey);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                groqApiUrl,
                HttpMethod.POST,
                httpEntity,
                String.class
            );

            return extractTextFromLLMResponse(response.getBody());

        } catch (Exception ex) {
            throw new LLMException(
                "Failed to communicate with LLM API. Please check your API key and try again. Details: " + ex.getMessage(),
                ex
            );
        }
    }

    private String extractTextFromLLMResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode contentNode = root
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

            if (contentNode.isMissingNode() || contentNode.isNull()) {
                throw new LLMException("LLM returned an empty response. Please try again.");
            }

            return contentNode.asText();

        } catch (LLMException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LLMException("Failed to parse LLM API response.", ex);
        }
    }
}
