package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.service.DocumentationService;
import com.arcengtr.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class AgentFactory {
    public static BaseAgent create(
            AgentConfig config,
            OpenAiClient openAiClient,
            Map<String, Tool> toolsMap,
            DocumentationService documentationService
    ) {
        boolean hasTool = toolsMap != null && !toolsMap.isEmpty();
        boolean hasDoc = documentationService != null;

        if (hasTool) {
            log.info("Creating ToolAgent: {} (action-based)", config.getName());
            return new ToolAgent(config, openAiClient, toolsMap);
        } else if (hasDoc) {
            log.info("Creating DocumentationAgent: {} (fact-based)", config.getName());
            return new DocumentationAgent(config, openAiClient, documentationService);
        } else {
            log.warn("Creating default ToolAgent for: {}", config.getName());
            return new ToolAgent(config, openAiClient, toolsMap != null ? toolsMap : Map.of());
        }
    }

    public static ToolAgent createToolAgent(
            AgentConfig config,
            OpenAiClient openAiClient,
            Map<String, Tool> toolsMap
    ) {
        log.info("Creating ToolAgent (explicit): {}", config.getName());
        return new ToolAgent(config, openAiClient, toolsMap != null ? toolsMap : Map.of());
    }

    public static DocumentationAgent createDocumentationAgent(
            AgentConfig config,
            OpenAiClient openAiClient,
            DocumentationService documentationService
    ) {
        log.info("Creating DocumentationAgent (explicit): {}", config.getName());
        return new DocumentationAgent(config, openAiClient, documentationService);
    }
}
