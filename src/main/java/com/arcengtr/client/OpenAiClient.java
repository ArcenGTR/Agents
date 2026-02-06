package com.arcengtr.client;

import com.arcengtr.model.ChatRequest;
import com.arcengtr.model.ChatResponse;
import com.arcengtr.model.ConversationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Slf4j
public class OpenAiClient {
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Builder
    public OpenAiClient(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.httpClient = httpClient != null ? httpClient : HttpClient.newHttpClient();
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    public String ask(
            List<ConversationMessage> messages,
            String model,
            double temperature
    ) throws Exception {
        return ask(messages, model, temperature, null);
    }

    public String ask(
            List<ConversationMessage> messages,
            String model,
            double temperature,
            Integer maxTokens
    ) throws Exception {
        ChatRequest chatRequest = ChatRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();

        String jsonBody = objectMapper.writeValueAsString(chatRequest);
        log.debug("Sending request to OpenAI: {}", jsonBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String errorMsg = "OpenAI API error (Status: " + response.statusCode() + "): " + response.body();
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        ChatResponse chatResponse = objectMapper.readValue(response.body(), ChatResponse.class);

        if (chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
            throw new RuntimeException("Empty response from OpenAI API");
        }

        return chatResponse.getChoices().get(0).getMessage().getContent();
    }

    public ChatResponse ask(
            List<ConversationMessage> messages,
            String model,
            double temperature,
            Integer maxTokens,
            List<Map<String, Object>> tools
    ) throws Exception {
        ChatRequest chatRequest = ChatRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .tools(tools)
                .build();

        String jsonBody = objectMapper.writeValueAsString(chatRequest);
        log.debug("Sending request to OpenAI: {}", jsonBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String errorMsg = "OpenAI API error (Status: " + response.statusCode() + "): " + response.body();
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        ChatResponse chatResponse = objectMapper.readValue(response.body(), ChatResponse.class);

        if (chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
            throw new RuntimeException("Empty response from OpenAI API");
        }

        return chatResponse;
    }
}
