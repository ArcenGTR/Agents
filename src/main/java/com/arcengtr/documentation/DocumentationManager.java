package com.arcengtr.documentation;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DocumentationManager {
    private final Map<String, String> documents = new HashMap<>();

    public void loadFromPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            log.warn("No documentation paths provided to load.");
            return;
        }

        for (String pathString : paths) {
            try {
                String content = Files.readString(Paths.get(pathString));
                String fileName = Paths.get(pathString).getFileName().toString().replace(".md", "");

                documents.put(fileName.toLowerCase(), content);
                log.info("Successfully loaded documentation: {}", fileName);
            } catch (IOException e) {
                log.error("Failed to read documentation file at {}: {}", pathString, e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error loading path {}: {}", pathString, e.getMessage());
            }
        }
    }

    public String searchDocumentation(String query) {
        if (query == null || query.isBlank()) return "";

        String[] queryWords = query.toLowerCase().split("\\s+");
        StringBuilder results = new StringBuilder();
        boolean found = false;

        for (Map.Entry<String, String> entry : documents.entrySet()) {
            String content = entry.getValue().toLowerCase();

            long matchCount = java.util.Arrays.stream(queryWords)
                    .filter(word -> word.length() > 2 && content.contains(word))
                    .count();

            if (matchCount > 0) {
                found = true;
                results.append("### Source: ").append(entry.getKey()).append(".md\n");

                String fullContent = entry.getValue();
                if (fullContent.length() > 1500) {
                    results.append(fullContent, 0, 1500).append("... [truncated]\n\n");
                } else {
                    results.append(fullContent).append("\n\n");
                }
            }
        }

        return found ? results.toString() : "No specific documentation found for this query.";
    }

    public String getDocumentation(String docName) {
        return documents.getOrDefault(docName.toLowerCase(), "Documentation not found for: " + docName);
    }
}