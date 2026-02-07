package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.config.AgentConfig;
import com.arcengtr.dto.ConversationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public abstract class BaseAgent {
    protected final AgentConfig config;
    protected final OpenAiClient openAiClient;
    protected final List<ConversationMessage> conversationHistory;

    public BaseAgent(AgentConfig config, OpenAiClient openAiClient) {
        this.config = config;
        this.openAiClient = openAiClient;
        this.conversationHistory = new CopyOnWriteArrayList<>();
    }

    public abstract String run(String userMessage) throws Exception;

    protected abstract String buildSystemPrompt();

    public void addToHistory(ConversationMessage message) {
        this.conversationHistory.add(message);
    }

    public String getAgentName() {
        return config.getName();
    }

    public String getAgentId() {
        return config.getId();
    }

    public void clearHistory() {
        conversationHistory.clear();
    }

    public abstract String getAgentType();

}
