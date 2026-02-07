package com.arcengtr.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class YamlConfig {
    private Map<String, AgentConfig> agents;
    private RoutingConfig routing;

    @Data
    public static class RoutingConfig {
        @JsonProperty("routing_model")
        private String routingModel;
        private double temperature;
        @JsonProperty("system_prompt")
        private String systemPrompt;
    }
}