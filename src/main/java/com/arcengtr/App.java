package com.arcengtr;

import com.arcengtr.config.AgentConfig;
import com.arcengtr.agent.AgentFactory;
import com.arcengtr.agent.BaseAgent;
import com.arcengtr.client.OpenAiClient;
import com.arcengtr.service.AgentLoaderService;
import com.arcengtr.config.SystemConfig;
import com.arcengtr.crew.SupportCrew;
import com.arcengtr.service.DocumentationService;
import com.arcengtr.service.ToolLoaderService;
import com.arcengtr.tool.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class App  {
    public static void main( String[] args ) throws Exception {

        try {
            log.info("Initializing Multi-Agent Support System...");

            // Initialize OpenAI client
            String apiKey = SystemConfig.getOpenAiApiKey();
            OpenAiClient openAiClient = OpenAiClient.builder()
                    .apiKey(apiKey)
                    .httpClient(HttpClient.newHttpClient())
                    .objectMapper(new ObjectMapper())
                    .build();

            log.info("OpenAI client initialized");

            AgentLoaderService loader = new AgentLoaderService();
            loader.loadFromResources("agents-config.yaml");
            AgentConfig techConfig = loader.getAgentConfig("technical_specialist");
            AgentConfig billConfig = loader.getAgentConfig("billing_specialist");

            log.info("Configs loaded for: {} and {}", techConfig.getName(), billConfig.getName());

            log.info(techConfig.toString());

            QdrantClient client = new QdrantClient(
                    QdrantGrpcClient.newBuilder(SystemConfig.getDbHostname(), 6334, false).build());

            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            DocumentationService techDocManager = new DocumentationService(client, embeddingModel);
            techDocManager.loadFromPaths(techConfig.getDocumentationSources());

            log.info("Documentation loaded for Technical Specialist");

            // Create billing tools
            ToolLoaderService toolLoader = new ToolLoaderService();
            Map<String, Tool> billingTools = toolLoader.loadTools("tools-config.yaml");

            log.info("Billing tools registered: {}", billingTools.keySet());

            BaseAgent technicalAgent = AgentFactory.createDocumentationAgent(
                    techConfig,
                    openAiClient,
                    techDocManager
            );

            BaseAgent billingAgent = AgentFactory.createToolAgent(
                    billConfig,
                    openAiClient,
                    billingTools
            );

            log.info("Agents created: {} and {}",
                    technicalAgent.getAgentName(),
                    billingAgent.getAgentName());

            // Create support crew
            SupportCrew supportCrew = new SupportCrew(technicalAgent, billingAgent, openAiClient);

            startInteractiveSession(supportCrew);

        } catch (Exception e) {
            log.error("Fatal error", e);
            System.exit(1);
        }
    }

    private static void startInteractiveSession(SupportCrew supportCrew) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("   Multi-Agent Support System - Interactive Console");
        System.out.println("=".repeat(70));
        System.out.println("Available Agents:");
        System.out.println("  • Technical Specialist - For API, deployment, troubleshooting");
        System.out.println("  • Billing Specialist - For refunds, subscriptions, pricing");
        System.out.println("\nCommands:");
        System.out.println("  'exit'    - Exit the application");
        System.out.println("  'history' - Show conversation history");
        System.out.println("  'clear'   - Clear conversation history");
        System.out.println("=".repeat(70) + "\n");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.isEmpty()) {
                continue;
            }

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("\n Thank you for using Multi-Agent Support System. Goodbye!\n");
                break;
            }

            if (userInput.equalsIgnoreCase("history")) {
                displayHistory(supportCrew);
                continue;
            }

            if (userInput.equalsIgnoreCase("clear")) {
                supportCrew.clearHistory();
                System.out.println("\n✓ Conversation history cleared.\n");
                continue;
            }

            try {
                System.out.println("\n   Processing your request...\n");
                String response = supportCrew.processUserMessage(userInput);
                System.out.println("Agent: " + response + "\n");
            } catch (Exception e) {
                log.error("Error processing message", e);
                System.out.println("  Error: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }

    private static void displayHistory(SupportCrew supportCrew) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("   Conversation History");
        System.out.println("=".repeat(70));
        supportCrew.getConversationHistory().forEach(msg ->
                System.out.println("[" + msg.getRole().toUpperCase() + "] " + msg.getContent())
        );
        System.out.println("=".repeat(70) + "\n");
    }
}
