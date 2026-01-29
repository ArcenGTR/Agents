package com.arcengtr.crew;

import com.arcengtr.agent.Agent;
import com.arcengtr.agent.AgentRouter;
import com.arcengtr.client.OpenAiClient;
import com.arcengtr.model.ConversationMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SupportCrew {
    private final Map<String, Agent> agents;
    private final AgentRouter router;
    private final List<ConversationMessage> sharedHistory;

    public SupportCrew(
            Agent technicalAgent,
            Agent billingAgent,
            OpenAiClient dispatcherClient
    ) {
        this.agents = new HashMap<>();
        this.agents.put(technicalAgent.getAgentId(), technicalAgent);
        this.agents.put(billingAgent.getAgentId(), billingAgent);
        this.router = new AgentRouter(technicalAgent, billingAgent, dispatcherClient);
        this.sharedHistory = new ArrayList<>();
    }

    public String processUserMessage(String userMessage) throws Exception {
        log.info("Processing user message: {}", userMessage);

        // Add to shared history
        ConversationMessage userMsg = ConversationMessage.user(userMessage);
        sharedHistory.add(userMsg);

        // Route to appropriate agent
        Agent selectedAgent = router.routeMessage(userMessage);
        log.info("Selected agent: {}", selectedAgent.getAgentName());

        // Run the agent
        String response = selectedAgent.run(userMessage);

        // Add agent response to shared history
        sharedHistory.add(ConversationMessage.assistant(response));

        return response;
    }

    public String processMultipleTurns(String... userMessages) throws Exception {
        String lastResponse = "";
        for (String message : userMessages) {
            lastResponse = processUserMessage(message);
            log.info("Response: {}", lastResponse);
        }
        return lastResponse;
    }

    public List<ConversationMessage> getConversationHistory() {
        return new ArrayList<>(sharedHistory);
    }

    public void clearHistory() {
        sharedHistory.clear();
        agents.values().forEach(Agent::clearHistory);
    }

    public Agent getAgent(String agentId) {
        return agents.get(agentId);
    }

    public Map<String, Agent> getAllAgents() {
        return new HashMap<>(agents);
    }

    public String getConversationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Conversation Summary ===\n");
        summary.append("Total turns: ").append(sharedHistory.size()).append("\n");
        summary.append("Agents involved: ");
        agents.values().stream()
                .map(Agent::getAgentName)
                .forEach(name -> summary.append(name).append(", "));
        summary.append("\n\nFull history:\n");
        for (ConversationMessage msg : sharedHistory) {
            summary.append("[").append(msg.getRole().toUpperCase()).append("] ")
                    .append(msg.getContent()).append("\n\n");
        }
        return summary.toString();
    }
}
