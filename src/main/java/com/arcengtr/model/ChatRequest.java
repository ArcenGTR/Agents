package com.arcengtr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String model;
    private List<ConversationMessage> messages;
    private double temperature;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    private List<Map<String, Object>> tools;
}