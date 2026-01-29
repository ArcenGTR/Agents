package com.arcengtr.documentation;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DocumentationManager {
    private final Map<String, String> documents;

    public DocumentationManager() {
        this.documents = new HashMap<>();
        loadDocumentation();
    }

    private void loadDocumentation() {
        // API Integration Documentation
        documents.put("api-integration", """
                # API Integration Guide
                
                ## Authentication
                - Use Bearer token authentication
                - Include Authorization header: "Bearer YOUR_API_KEY"
                - API key can be generated from dashboard settings
                
                ## Base URL
                - Production: https://api.company.com/v1
                - Staging: https://staging-api.company.com/v1
                
                ## Rate Limiting
                - 1000 requests per minute for standard tier
                - 5000 requests per minute for enterprise tier
                - Rate limit info in response headers: X-RateLimit-Remaining
                
                ## Common Endpoints
                - POST /auth/token - Get authentication token
                - GET /users/{id} - Retrieve user information
                - POST /orders - Create new order
                - GET /orders/{id} - Get order details
                """);

        // Troubleshooting Documentation
        documents.put("troubleshooting", """
                # Troubleshooting Guide
                
                ## 401 Unauthorized Error
                Solution: Verify API key is correct and not expired
                - Check dashboard for valid API key
                - Ensure Authorization header format is correct
                - Generate new API key if needed
                
                ## 429 Rate Limit Error
                Solution: Implement exponential backoff and request queuing
                - Wait 60 seconds before retrying
                - Reduce concurrent request count
                - Consider upgrading to enterprise tier
                
                ## Connection Timeout
                Solution: Check network connectivity and endpoint availability
                - Verify base URL is correct
                - Check if service is in maintenance
                - Implement connection timeout of 30 seconds
                - Use retry mechanism with exponential backoff
                
                ## JSON Parse Error
                Solution: Validate response format
                - Ensure response Content-Type is application/json
                - Check for special characters in data
                - Validate JSON structure with validator
                """);

        // Deployment Documentation
        documents.put("deployment", """
                # Deployment Guide
                
                ## Prerequisites
                - Java 17+
                - Docker (optional)
                - Kubernetes (for production)
                
                ## Environment Variables
                - API_KEY: OpenAI API key
                - SERVICE_PORT: Server port (default: 8080)
                - LOG_LEVEL: Logging level (DEBUG, INFO, WARN)
                
                ## Deployment Steps
                1. Build: mvn clean package
                2. Test: mvn test
                3. Docker: docker build -t support-system .
                4. Run: docker run -e API_KEY=xxx -p 8080:8080 support-system
                
                ## Health Check
                - Endpoint: GET /health
                - Expected response: {"status": "UP"}
                - Implement every 30 seconds in production
                """);

        // FAQ Documentation
        documents.put("faq", """
                # Frequently Asked Questions
                
                ## How do I get started?
                1. Create an account on our dashboard
                2. Generate an API key from settings
                3. Review API documentation
                4. Test with sample requests
                
                ## What are the system requirements?
                - Minimum: Java 11, 2GB RAM
                - Recommended: Java 17+, 4GB RAM
                - Network: Stable internet connection
                
                ## How long does processing take?
                - Standard requests: < 1 second
                - Batch operations: 5-30 seconds depending on volume
                - Async operations: Variable, check status endpoint
                
                ## Can I test before production?
                Yes! Use our staging environment:
                https://staging-api.company.com/v1
                Same credentials work for staging.
                """);
    }

    public String getDocumentation(String docName) {
        String doc = documents.get(docName.toLowerCase());
        if (doc == null) {
            log.warn("Documentation not found: {}", docName);
            return "Documentation not found for: " + docName;
        }
        return doc;
    }

    public String searchDocumentation(String query) {
        query = query.toLowerCase();
        StringBuilder results = new StringBuilder();

        for (Map.Entry<String, String> entry : documents.entrySet()) {
            if (entry.getValue().toLowerCase().contains(query)) {
                results.append("Found in ").append(entry.getKey()).append(":\n");
                // Extract relevant section (first 500 chars containing the query)
                String content = entry.getValue();
                int idx = content.toLowerCase().indexOf(query);
                int start = Math.max(0, idx - 100);
                int end = Math.min(content.length(), idx + 400);
                results.append(content, start, end).append("\n\n");
            }
        }

        return results.length() > 0 ? results.toString() : "No documentation found for: " + query;
    }

    public String getAllDocumentationKeys() {
        return String.join(", ", documents.keySet());
    }
}
