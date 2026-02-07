package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.config.AgentConfig;
import com.arcengtr.dto.ChatResponse;
import com.arcengtr.dto.ConversationMessage;
import com.arcengtr.dto.ToolCall;
import com.arcengtr.tool.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        List<ConversationMessage> messages = new ArrayList<>();
        messages.add(ConversationMessage.system(buildSystemPrompt()));
        messages.addAll(conversationHistory);
        messages.add(ConversationMessage.user(userMessage));

        List<Map<String, Object>> nativeTools = toolsMap.values().stream()
                .map(Tool::toJsonSchemaMap)
                .toList();

        int iteration = 0;
        while (iteration < MAX_ITERATIONS) {
            String response = openAiClient.ask(
                    messages,
                    config.getModel(),
                    config.getTemperature(),
                    config.getMaxCompletionTokens()
            );

            ChatResponse chatResponse = openAiClient.ask(
                    messages,
                    config.getModel(),
                    0.3,
                    1000,
                    nativeTools);
            ConversationMessage assistantMessage = chatResponse.getChoices().get(0).getMessage();

            ConversationMessage cm = ConversationMessage.assistant(assistantMessage.getContent());
            cm.setToolCalls(assistantMessage.getToolCalls());

            messages.add(cm);

            log.info("[{}] Iteration {}: {}", config.getName(), iteration, response);

            if (assistantMessage.getToolCalls() != null && !assistantMessage.getToolCalls().isEmpty()) {
                for (ToolCall call : assistantMessage.getToolCalls()) {
                    String functionName = call.getFunction().getName();
                    String argumentsJson = call.getFunction().getArguments();

                    Map<String, Object> args = objectMapper.readValue(argumentsJson, Map.class);

                    log.info("Native execution: {}", functionName);
                    Object result = toolsMap.get(functionName).execute(args);

                    messages.add(ConversationMessage.builder()
                            .role("tool")
                            .toolCallId(call.getId())
                            .content(objectMapper.writeValueAsString(result))
                            .build());
                }
                iteration++;
            } else {
                String finalResponse = assistantMessage.getContent();
                addToHistory(ConversationMessage.user(userMessage));
                addToHistory(ConversationMessage.assistant(finalResponse));
                return finalResponse;
            }
        }

        return "Max iterations reached";
    }

    @Override
    protected String buildSystemPrompt() {

        return config.getSystemPrompt() + "\n\n" +
                "[USER CONTEXT]\n" +
                "- Current User ID: USER-12345\n" +
                "- User Email: customer@example.com\n" +
                "- Authentication Status: VERIFIED\n\n" +
                "Use tools to fetch missing information.";  // Mock user info
    }

    @Override
    public String getAgentType() {
        return "ToolAgent (Action-based, can use " + toolsMap.size() + " tools)";
    }
}
