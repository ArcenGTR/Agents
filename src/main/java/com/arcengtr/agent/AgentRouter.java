package com.arcengtr.agent;

import com.arcengtr.client.OpenAiClient;
import com.arcengtr.config.AgentConfig;
import com.arcengtr.dto.ConversationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class AgentRouter {
    private final BaseAgent technicalAgent;
    private final BaseAgent billingAgent;
    private final OpenAiClient dispatcherClient;

    private static final Map<String, AgentConfig.AgentRole> KEYWORD_PATTERNS = new HashMap<>();

    static {
        // Billing
        KEYWORD_PATTERNS.put("(?i).*(refund|payment|invoice|billing|charge|cost|price|subscription|plan|ticket).*",
                AgentConfig.AgentRole.BILLING);
        KEYWORD_PATTERNS.put("(?i).*(cancel subscription|upgrade plan|downgrade|payment method|open ticket).*",
                AgentConfig.AgentRole.BILLING);

        // Technical
        KEYWORD_PATTERNS.put("(?i).*(error|bug|crash|timeout|connection|api|integration|code|debug).*",
                AgentConfig.AgentRole.TECHNICAL);
        KEYWORD_PATTERNS.put("(?i).*(how do i|how to|tutorial|guide|documentation|deploy).*",
                AgentConfig.AgentRole.TECHNICAL);
    }

    public AgentRouter(BaseAgent technicalAgent, BaseAgent billingAgent, OpenAiClient dispatcherClient) {
        this.technicalAgent = technicalAgent;
        this.billingAgent = billingAgent;
        this.dispatcherClient = dispatcherClient;
    }

    public BaseAgent routeMessage(String userMessage) throws Exception {
        log.info("Routing message: {}", userMessage);

        AgentConfig.AgentRole detectedRole = detectRoleByKeywords(userMessage);
        if (detectedRole != null) {
            log.info("Routed by keyword matching to: {}", detectedRole);
            return selectAgent(detectedRole);
        }

        return routeByLLM(userMessage);
    }

    private AgentConfig.AgentRole detectRoleByKeywords(String message) {
        for (Map.Entry<String, AgentConfig.AgentRole> entry : KEYWORD_PATTERNS.entrySet()) {
            if (Pattern.matches(entry.getKey(), message)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private BaseAgent routeByLLM(String userMessage) throws Exception {
        log.info("Using LLM for routing decision");

        String routingPrompt = """
                You are a routing agent. Analyze the user message and determine which agent should handle it.
                
                Available agents:
                1. TECHNICAL_SPECIALIST - Handles technical issues, API questions, integration, deployment, troubleshooting
                2. BILLING_SPECIALIST - Handles billing, refunds, payments, subscriptions, pricing
                
                User message: "%s"
                
                Respond with ONLY one word: TECHNICAL_SPECIALIST or BILLING_SPECIALIST
                """.formatted(userMessage);

        String result = dispatcherClient.ask(
                List.of(ConversationMessage.user(routingPrompt)),
                "gpt-4o-mini",
                0.3
        );

        if (result.toUpperCase().contains("BILLING")) {
            return billingAgent;
        }
        return technicalAgent;
    }

    private BaseAgent selectAgent(AgentConfig.AgentRole role) {
        return switch (role) {
            case BILLING -> billingAgent;
            case TECHNICAL -> technicalAgent;
            default -> technicalAgent;
        };
    }
}
