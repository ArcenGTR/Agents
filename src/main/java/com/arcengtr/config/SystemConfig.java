package com.arcengtr.config;

import com.arcengtr.agent.AgentConfig;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SystemConfig {
    public static String getOpenAiApiKey() {
        Dotenv env = Dotenv.load();
        String apiKey = env.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("OPENAI_API_KEY environment variable not set");
            throw new RuntimeException("OPENAI_API_KEY environment variable is required");
        }
        return apiKey;
    }
}
