package com.arcengtr.config;

import com.arcengtr.exception.KeyNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemConfig {
    public static String getOpenAiApiKey() {
        Dotenv env = Dotenv.load();
        String apiKey = env.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("OPENAI_API_KEY environment variable not set");
            throw new KeyNotFoundException("OPENAI_API_KEY environment variable is required");
        }
        return apiKey;
    }

    public static String getDbHostname() {
        Dotenv env = Dotenv.load();
        log.info("QDRANT_HOSTNAME: {}", env.get("QDRANT_HOSTNAME"));
        return env.get("QDRANT_HOSTNAME") != null ? env.get("QDRANT_HOSTNAME") : "localhost";
    }
}
