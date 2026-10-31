package com.testpilot.service;

import com.openai.client.OpenAIClient;
import com.openai.client.OpenAIClientImpl;
import com.openai.core.ClientOptions;
import com.testpilot.exception.LLMException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.http.okhttp.SpringAiOpenAiHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMService {

    @Value("${spring.ai.openai.chat.options.model}")
    private String groqModel;

    @Value("${spring.ai.openai.base-url}")
    private String groqBaseUrl;

    @Value("${spring.ai.openai.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.openai.chat.options.max-tokens:3500}")
    private Integer maxTokens;

    /**
     * Calls the Groq API (OpenAI-compatible) via Spring AI's ChatClient to generate
     * LLM content.
     * The user-supplied API key is scoped per request and never stored.
     *
     * @param prompt     The engineered prompt to send to the model.
     * @param groqApiKey The user-supplied Groq API key forwarded from the request
     *                   header.
     * @return The raw text content returned by the LLM.
     */
    public String generateContent(String prompt, String groqApiKey) {
        try {
            // Build per-request OpenAI client using user's API key, Groq base URL, and
            // Spring AI HTTP client
            ClientOptions clientOptions = ClientOptions.builder()
                    .baseUrl(groqBaseUrl)
                    .apiKey(groqApiKey)
                    .httpClient(SpringAiOpenAiHttpClient.builder().build())
                    .build();

            OpenAIClient openAiClient = new OpenAIClientImpl(clientOptions);

            // Configure chat options with low reasoning effort and JSON mode enforcement
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                    .model(groqModel)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .reasoningEffort("low")
                    .responseFormat(OpenAiChatModel.ResponseFormat.builder()
                            .type(OpenAiChatModel.ResponseFormat.Type.JSON_OBJECT)
                            .build())
                    .build();

            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .openAiClient(openAiClient)
                    .openAiClientAsync(openAiClient.async())
                    .options(chatOptions)
                    .build();

            // Execute the prompt via Spring AI's fluent ChatClient API
            String response = ChatClient.create(chatModel)
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (response == null || response.isBlank()) {
                throw new LLMException("LLM returned an empty response. Please try again.");
            }

            return response;

        } catch (LLMException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LLMException(
                    "Failed to communicate with LLM API. Please check your API key and try again. Details: "
                            + ex.getMessage(),
                    ex);
        }
    }
}
