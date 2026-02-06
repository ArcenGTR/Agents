package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.service.DocumentationService;
import com.arcengtr.model.ConversationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DocumentationAgent extends BaseAgent {
    private final DocumentationService documentationService;

    public DocumentationAgent(
            AgentConfig config,
            OpenAiClient openAiClient,
            DocumentationService documentationService
    ) {
        super(config, openAiClient);
        this.documentationService = documentationService;
    }

    @Override
    public String run(String userMessage) throws Exception {
        log.info("[{}] Running (DocumentationAgent) with message: {}", config.getName(), userMessage);

        String hypotheticalAnswer = generateHypotheticalAnswer(userMessage);
        log.debug("HyDE Answer: {}", hypotheticalAnswer);

        // Search for relevant documentation
        String relevantDocs = documentationService.searchDocumentation(hypotheticalAnswer);
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

    private String generateHypotheticalAnswer(String query) {
        String hydePrompt = String.format(
                "Write a two sentences that would answer this question. " +
                        "Focus on technical keywords and facts. Question: %s", query);

        try {
            return openAiClient.ask(
                    List.of(ConversationMessage.user(hydePrompt)),
                    "gpt-4o-mini",
                    0.1,
                    150
            );
        } catch (Exception e) {
            log.warn("HyDE generation failed, falling back to original query", e);
            return query;
        }
    }

    @Override
    public String getAgentType() {
        return "DocumentationAgent (Read-only, fact-based support)";
    }
}
