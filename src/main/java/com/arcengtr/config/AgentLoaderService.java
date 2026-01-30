package com.arcengtr.config;

import com.arcengtr.agent.AgentConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class AgentLoaderService {
    private final ObjectMapper yamlMapper;

    @Getter
    private YamlConfig config;

    public AgentLoaderService() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.yamlMapper.findAndRegisterModules();
    }

    public void loadFromResources(String fileName) {
        log.info("Loading agents configuration from: {}", fileName);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("YAML file not found in resources: " + fileName);
            }
            this.config = yamlMapper.readValue(is, YamlConfig.class);
            log.info("Successfully loaded {} agents", config.getAgents().size());
        } catch (Exception e) {
            log.error("Critical error loading YAML configuration", e);
            throw new RuntimeException("Failed to initialize Agent Configuration", e);
        }
    }

    public AgentConfig getAgentConfig(String agentId) {
        return config.getAgents().get(agentId);
    }
}
