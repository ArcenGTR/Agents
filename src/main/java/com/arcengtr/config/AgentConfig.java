package com.arcengtr.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentConfig {
    private String id;
    private String name;
    private String description;
    private AgentRole role;
    private String model;
    private double temperature;

    @JsonProperty("max_completion_tokens")
    private int maxCompletionTokens;

    @JsonProperty("system_prompt")
    private String systemPrompt;

    @JsonProperty("documentation_sources")
    private List<String> documentationSources;

    public enum AgentRole {
        TECHNICAL,
        BILLING,
        DISPATCHER
    }
}
