package com.arcengtr;

import com.arcengtr.agent.Agent;
import com.arcengtr.client.OpenAiClient;
import com.arcengtr.config.SystemConfig;
import com.arcengtr.crew.SupportCrew;
import com.arcengtr.tool.BillingTools;
import com.arcengtr.tool.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class App  {
    public static void main( String[] args ) throws Exception {

        // -------------------- 3 version ------------------------------

/*        Dotenv env = Dotenv.load();
        final String OPENAI_API_KEY = env.get("OPENAI_API_KEY");

        AgentsConfig config = ConfigLoader.loadYamlConfig("agents-config.yaml");

        OpenAiClient client = OpenAiClient.builder()
                .apiKey(OPENAI_API_KEY)
                .build();

        OpenAiService openAiService = new OpenAiService(client);
        DocumentService documentService = new DocumentService();

        BillingService billingService = new BillingService();
        ToolService toolService = new ToolService(billingService);

        AgentFactory factory = new AgentFactory(openAiService, documentService, toolService);

        Agent technicalAgent = factory.create(config.getAgents().get("technical_specialist"));

        ConversationContext context = new ConversationContext();
        context.setCurrentAgentId(technicalAgent.getConfig().getId());

        // ---- Turn 1 ----
        String user1 = "How do I authenticate using an API key in Java?";
        context.addMessage(new ConversationMessage("user", user1));

        AgentResponse r1 = technicalAgent.process(user1, context);
        System.out.println("\nAssistant:\n" + r1.getMessage());
        context.addMessage(new ConversationMessage("assistant", r1.getMessage()));

        // ---- Turn 2 (context-aware) ----
        String user2 = "What should I do if I get a 401 error?";
        context.addMessage(new ConversationMessage("user", user2));

        AgentResponse r2 = technicalAgent.process(user2, context);
        System.out.println("\nAssistant:\n" + r2.getMessage());
        context.addMessage(new ConversationMessage("assistant", r2.getMessage()));





//        Agent billingAgent = factory.create(config.getAgents().get("billing_specialist"));
//        context.setCurrentAgentId(billingAgent.getConfig().getId());
//
//        String billUser1 = "Hi! I’m not sure what I was charged for. What is my current subscription plan?";
//        String billUser1 = "Can you help me debug my OAuth callback?\n";
//        context.addMessage(new ConversationMessage("user", billUser1));
//
//        AgentResponse br1 = billingAgent.process(billUser1, context);
//        System.out.println("\n[Billing] Assistant:\n" + br1.getMessage());
//        context.addMessage(new ConversationMessage("assistant", br1.getMessage()));

//        String billUser2 = "I bought a subscription just 4 days ago, but I’ve changed my mind. Can I get a full refund?";
//        context.addMessage(new ConversationMessage("user", billUser2));
//
//        AgentResponse br2 = billingAgent.process(billUser2, context);
//        System.out.println("\n[Billing] Assistant:\n" + br2.getMessage());
//        context.addMessage(new ConversationMessage("assistant", br2.getMessage()));
//
//        String billUser3 = "Yes";
//        context.addMessage(new ConversationMessage("user", billUser3));
//
//        AgentResponse br3 = billingAgent.process(billUser3, context);
//        System.out.println("\n[Billing] Assistant:\n" + br3.getMessage());
//        context.addMessage(new ConversationMessage("assistant", br3.getMessage()));
//
//        String billUser4 = "May you send it to my email?";
//        context.addMessage(new ConversationMessage("user", billUser4));
//
//        AgentResponse br4 = billingAgent.process(billUser4, context);
//        System.out.println("\n[Billing] Assistant:\n" + br4.getMessage());
//        context.addMessage(new ConversationMessage("assistant", br4.getMessage()));
//
//        String billUser5 = "Yes, send it to 123@gmail.com";
//        context.addMessage(new ConversationMessage("user", billUser5));
//
//        AgentResponse br5 = billingAgent.process(billUser5, context);
//        System.out.println("\n[Billing] Assistant:\n" + br5.getMessage());
//        context.addMessage(new ConversationMessage("assistant", br5.getMessage()));
//
//        String billUser6 = "May you show my current plan?";
//        context.addMessage(new ConversationMessage("user", billUser5));
//
//        AgentResponse br6 = billingAgent.process(billUser6, context);
//        System.out.println("\n[Billing] Assistant:\n" + br6.getMessage());
//        context.addMessage(new ConversationMessage("assistant", br6.getMessage()));
//
//        String billUser7 = "Yeah, go on";
//        context.addMessage(new ConversationMessage("user", billUser7));
//
//        AgentResponse br7 = billingAgent.process(billUser7, context);
//        System.out.println("\n[Billing] Assistant:\n" + br7.getMessage());
//        context.addMessage(new ConversationMessage("assistant", br7.getMessage()));*/

        // ----------------- version 4 ---------------

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

            // Create billing tools
            Map<String, Tool> billingTools = new HashMap<>();
            billingTools.put("process_refund", new BillingTools.ProcessRefundTool());
            billingTools.put("send_refund_form", new BillingTools.SendRefundFormTool());
            billingTools.put("get_plan_info", new BillingTools.GetPlanInfoTool());
            billingTools.put("open_support_case", new BillingTools.OpenSupportCaseTool());

            log.info("Billing tools registered");

            // Create agents
            Agent technicalAgent = new Agent(
                    SystemConfig.getTechnicalSpecialistConfig(),
                    openAiClient,
                    new HashMap<>()
            );

            Agent billingAgent = new Agent(
                    SystemConfig.getBillingSpecialistConfig(),
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
