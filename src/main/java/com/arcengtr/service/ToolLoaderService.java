package com.arcengtr.service;

import com.arcengtr.tool.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ToolLoaderService {
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public Map<String, Tool> loadTools(String resourcePath) throws Exception {
        Map<String, Tool> toolsMap = new HashMap<>();

        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        JsonNode root = yamlMapper.readTree(is);
        JsonNode toolsNode = root.get("tools");

        for (JsonNode node : toolsNode) {
            String className = node.get("implementationClass").asText();

            Tool tool = (Tool) Class.forName(className).getDeclaredConstructor().newInstance();

            yamlMapper.readerForUpdating(tool).readValue(node);

            toolsMap.put(tool.getName(), tool);
            log.info("Loaded tool: {} [{}]", tool.getName(), className);
        }

        return toolsMap;
    }
}
