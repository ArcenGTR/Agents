package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.documentation.DocumentationManager;
import com.arcengtr.model.ConversationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DocumentationAgent extends BaseAgent {
    private final DocumentationManager documentationManager;

    public DocumentationAgent(
            AgentConfig config,
            OpenAiClient openAiClient,
            DocumentationManager documentationManager
    ) {
        super(config, openAiClient);
        this.documentationManager = documentationManager;
    }

    @Override
    public String run(String userMessage) throws Exception {
        log.info("[{}] Running (DocumentationAgent) with message: {}", config.getName(), userMessage);

        // Search for relevant documentation
        String relevantDocs = documentationManager.searchDocumentation(userMessage);
        log.debug("Found relevant documentation: {}", relevantDocs.substring(0, Math.min(100, relevantDocs.length())));

        // Build messages with documentation context
        List<ConversationMessage> messages = new ArrayList<>();
        messages.add(ConversationMessage.system(buildSystemPrompt()));
        messages.addAll(conversationHistory);

        // Add user message with documentation context
        String userMessageWithContext = userMessage + "\n\n[DOCUMENTATION CONTEXT]\n" + relevantDocs;
        messages.add(ConversationMessage.user(userMessageWithContext));

        // Get response from LLM
        String response = openAiClient.ask(
                messages,
                config.getModel(),
                config.getTemperature(),
                config.getMaxCompletionTokens()
        );

        // Store in history
        addToHistory(ConversationMessage.user(userMessage));
        addToHistory(ConversationMessage.assistant(response));

        return response;
    }

    @Override
    protected String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder(config.getSystemPrompt());

        if (config.getDocumentationSources() != null && !config.getDocumentationSources().isEmpty()) {
            prompt.append("\n\nAvailable documentation sections:\n");
            for (String source : config.getDocumentationSources()) {
                prompt.append("- ").append(source).append("\n");
            }
        }

        return prompt.toString();
    }

    @Override
    public String getAgentType() {
        return "DocumentationAgent (Read-only, fact-based support)";
    }

    @Override
    public boolean hasTool() {
        return false;
    }

    @Override
    public boolean usesDocumentation() {
        return true;
    }

    public String getDocumentation(String docName) {
        return documentationManager.getDocumentation(docName);
    }

    public String searchDocumentation(String query) {
        return documentationManager.searchDocumentation(query);
    }
}
