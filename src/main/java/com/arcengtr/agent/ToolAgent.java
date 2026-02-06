package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.model.ConversationMessage;
import com.arcengtr.tool.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ToolAgent extends BaseAgent {

    private static final int MAX_ITERATIONS = 5;

    private final Map<String, Tool> toolsMap;
    private final ObjectMapper objectMapper;

    public ToolAgent(
            AgentConfig config,
            OpenAiClient openAiClient,
            Map<String, Tool> toolsMap
    ) {
        super(config, openAiClient);
        this.toolsMap = toolsMap;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String run(String userMessage) throws Exception {
        log.info("[{}] Processing message: {}", config.getName(), userMessage);

        // ReAct Loop
        List<ConversationMessage> messages = new ArrayList<>();
        messages.add(ConversationMessage.system(buildSystemPrompt()));
        messages.addAll(conversationHistory); // Берем всю прошлую историю
        messages.add(ConversationMessage.user(userMessage));

        String finalAssistantMessage = "";
        int iteration = 0;

        while (iteration < MAX_ITERATIONS) {
            String response = openAiClient.ask(
                    messages,
                    config.getModel(),
                    config.getTemperature(),
                    config.getMaxCompletionTokens()
            );

            log.info("[{}] Iteration {}: {}", config.getName(), iteration, response);

            if (hasToolCalls(response)) {
                String toolResults = executeAllToolsInResponse(response);

                messages.add(ConversationMessage.assistant(response));

                String contextMessage = String.format(
                        "SYSTEM: Tool execution results:\n%s\n" +
                                "Use this data to answer the user or decide if another tool call is needed.",
                        toolResults
                );
                messages.add(ConversationMessage.system(contextMessage));

                iteration++;
            } else {
                finalAssistantMessage = response;
                break;
            }
        }

        addToHistory(ConversationMessage.user(userMessage));
        addToHistory(ConversationMessage.assistant(finalAssistantMessage));

        return finalAssistantMessage;
    }

    @Override
    protected String buildSystemPrompt() {
        return config.getSystemPrompt() + "\n\n" +
                "[USER CONTEXT]\n" +
                "- Current User ID: USER-12345\n" +
                "- User Email: customer@example.com\n" +
                "- Authentication Status: VERIFIED\n\n" +
                buildToolsDefinition() +
                "\n## Tool Usage Instructions\n" +
                "Use tools to fetch missing information. Format: <tool_call>{\"name\": \"...\", \"arguments\": {...}}</tool_call>";
    }

    private String buildToolsDefinition() {
        if (toolsMap.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("## Available Tools\n");
        for (Tool tool : toolsMap.values()) {
            sb.append(tool.toJsonSchema()).append("\n");
        }
        return sb.toString();
    }

    private boolean hasToolCalls(String response) {
        return response.contains("<tool_call>");
    }

    private String executeAllToolsInResponse(String response) {
        Pattern pattern = Pattern.compile("<tool_call>\\s*([^<]+)\\s*</tool_call>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        StringBuilder allResults = new StringBuilder();

        while (matcher.find()) {
            String json = matcher.group(1).trim();
            try {
                Map<String, Object> call = objectMapper.readValue(json, Map.class);
                String name = (String) call.get("name");
                Map<String, Object> args = (Map<String, Object>) call.get("arguments");

                if (toolsMap.containsKey(name)) {
                    log.info("Executing: {}", name);
                    Object result = toolsMap.get(name).execute(args);
                    allResults.append("\n[Result of ").append(name).append("]: ").append(result);
                }
            } catch (Exception e) {
                allResults.append("\n[Error executing tool]: ").append(e.getMessage());
            }
        }
        return allResults.toString();
    }


    public Object executeTool(String toolName, Map<String, Object> arguments) throws Exception {
        if (!toolsMap.containsKey(toolName)) {
            throw new IllegalArgumentException("Tool not found: " + toolName);
        }
        Tool tool = toolsMap.get(toolName);
        log.info("Directly executing tool: {}", toolName);
        return tool.execute(arguments);
    }

    public Map<String, Tool> getTools() {
        return toolsMap;
    }

    public List<String> getToolNames() {
        return new ArrayList<>(toolsMap.keySet());
    }

    @Override
    public String getAgentType() {
        return "ToolAgent (Action-based, can use " + toolsMap.size() + " tools)";
    }
}
