package com.arcengtr.crew;

import com.arcengtr.agent.AgentRouter;
import com.arcengtr.agent.BaseAgent;
import com.arcengtr.client.OpenAiClient;
import com.arcengtr.model.ConversationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SupportCrew {
    private final Map<String, BaseAgent> agents;
    private final AgentRouter router;
    private final List<ConversationMessage> sharedHistory;

    public SupportCrew(
            BaseAgent technicalAgent,
            BaseAgent billingAgent,
            OpenAiClient openAiClient
    ) {
        this.agents = new HashMap<>();
        this.agents.put(technicalAgent.getAgentId(), technicalAgent);
        this.agents.put(billingAgent.getAgentId(), billingAgent);
        this.router = new AgentRouter(technicalAgent, billingAgent, openAiClient);
        this.sharedHistory = new ArrayList<>();
    }

    public String processUserMessage(String userMessage) throws Exception {
        log.info("Processing user message: {}", userMessage);

        ConversationMessage userMsg = ConversationMessage.user(userMessage);
        sharedHistory.add(userMsg);

        BaseAgent selectedAgent = router.routeMessage(userMessage);
        log.info("Selected agent: {} [{}]", selectedAgent.getAgentName(), selectedAgent.getAgentType());

        String response = selectedAgent.run(userMessage);

        if (response.contains("colleague from the Billing")) {
            log.info("Hand-off detected! Automatically re-routing to Billing Specialist...");

            BaseAgent billingAgent = agents.get("tech_specialist".equals(selectedAgent.getAgentId()) ? "billing_specialist" : "tech_specialist");

            if (billingAgent != null) {
                response = billingAgent.run(userMessage);
            }
        }

        sharedHistory.add(ConversationMessage.assistant(response));

        return response;
    }

    public List<ConversationMessage> getConversationHistory() {
        return new ArrayList<>(sharedHistory);
    }

    public void clearHistory() {
        sharedHistory.clear();
        agents.values().forEach(BaseAgent::clearHistory);
    }
}
