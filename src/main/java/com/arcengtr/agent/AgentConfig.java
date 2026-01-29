package com.arcengtr.agent;

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
    private int maxCompletionTokens;
    private String systemPrompt;
    private List<String> documentationSources;

    public enum AgentRole {
        TECHNICAL,
        BILLING,
        DISPATCHER
    }
}
