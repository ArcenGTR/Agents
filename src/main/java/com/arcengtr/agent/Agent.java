package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.model.ConversationMessage;
import com.arcengtr.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class Agent {
    private final AgentConfig config;
    private final OpenAiClient openAiClient;
    private final List<ConversationMessage> conversationHistory;
    private final Map<String, Tool> toolsMap;

    public Agent(AgentConfig config, OpenAiClient openAiClient, Map<String, Tool> toolsMap) {
        this.config = config;
        this.openAiClient = openAiClient;
        this.toolsMap = toolsMap;
        this.conversationHistory = new CopyOnWriteArrayList<>();
    }

    public void addToHistory(ConversationMessage message) {
        this.conversationHistory.add(message);
    }

    public void addMultipleToHistory(List<ConversationMessage> messages) {
        this.conversationHistory.addAll(messages);
    }

    public List<ConversationMessage> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    public String run(String userMessage) throws Exception {
        log.info("[{}] Running with message: {}", config.getName(), userMessage);

        // Build messages for this agent
        List<ConversationMessage> messages = new ArrayList<>();
        messages.add(ConversationMessage.system(buildSystemPromptWithTools(config.getSystemPrompt())));
        messages.addAll(conversationHistory);
        messages.add(ConversationMessage.user(userMessage));

        // Get response from LLM
        String response = openAiClient.ask(
                messages,
                config.getModel(),
                config.getTemperature(),
                config.getMaxCompletionTokens()
        );

        //log.info("[{}] Response: {}", config.getName(), response);
        log.info("[{}] Response received", config.getName());

        response = processToolCalls(response);

        // Store in history
        addToHistory(ConversationMessage.user(userMessage));
        addToHistory(ConversationMessage.assistant(response));

        return response;
    }

    private String buildSystemPromptWithTools(String systemPrompt) {
        StringBuilder prompt = new StringBuilder(systemPrompt);

        if (!toolsMap.isEmpty()) {
            prompt.append("\n\n## Available Tools\n\n");
            for (Tool tool : toolsMap.values()) {
                prompt.append(tool.toJsonSchema()).append("\n");
            }

            prompt.append("""
                
                ## Tool Usage Instructions
                When you need to use a tool, respond with the following format:
                
                <tool_call>
                {"name": "tool_name", "arguments": {"param": "value"}}
                </tool_call>
                
                """);
        }

        return prompt.toString();
    }

    private String processToolCalls(String response) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "<tool_call>\\s*([^<]+)\\s*</tool_call>",
                java.util.regex.Pattern.DOTALL
        );
        java.util.regex.Matcher matcher = pattern.matcher(response);

        StringBuilder result = new StringBuilder(response);

        while (matcher.find()) {
            String toolCallJson = matcher.group(1).trim();
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper =
                        new com.fasterxml.jackson.databind.ObjectMapper();

                @SuppressWarnings("unchecked")
                Map<String, Object> toolCall = mapper.readValue(
                        toolCallJson,
                        Map.class
                );

                String toolName = (String) toolCall.get("name");
                @SuppressWarnings("unchecked")
                Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");

                if (toolsMap.containsKey(toolName)) {
                    Tool tool = toolsMap.get(toolName);
                    log.info("Executing tool: {}", toolName);

                    Object toolResult = tool.execute(arguments);
                    log.info("Tool result: {}", toolResult);

                    // Replace tool call with result in response
                    String toolOutput = "\n\n**Tool Result (" + toolName + "):**\n" +
                            toolResult.toString() + "\n";
                    result.append(toolOutput);
                }
            } catch (Exception e) {
                log.error("Error processing tool call", e);
            }
        }

        return result.toString();
    }

    public String getAgentName() {
        return config.getName();
    }

    public String getAgentId() {
        return config.getId();
    }

    public AgentConfig.AgentRole getRole() {
        return config.getRole();
    }

    public Map<String, Tool> getTools() {
        return toolsMap;
    }

    public String getSystemPrompt() {
        return config.getSystemPrompt();
    }

    public void clearHistory() {
        conversationHistory.clear();
    }
}
