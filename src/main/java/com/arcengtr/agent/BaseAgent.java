package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.model.ConversationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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

    public void addMultipleToHistory(List<ConversationMessage> messages) {
        this.conversationHistory.addAll(messages);
    }

    public List<ConversationMessage> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
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

    public String getSystemPrompt() {
        return config.getSystemPrompt();
    }

    public void clearHistory() {
        conversationHistory.clear();
    }

    public abstract String getAgentType();

    public abstract boolean hasTool();

    public abstract boolean usesDocumentation();
}
