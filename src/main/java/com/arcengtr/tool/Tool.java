package com.arcengtr.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class Tool {
    protected String name;
    protected String description;
    protected Map<String, String> parameters;

    public Tool(String name, String description) {
        this.name = name;
        this.description = description;
        this.parameters = new HashMap<>();
    }

    public abstract Object execute(Map<String, Object> arguments) throws Exception;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    protected void addParameter(String paramName, String paramType, String description) {
        parameters.put(paramName, paramType);
    }

    public String toJsonSchema() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"name\": \"").append(name).append("\",\n");
        sb.append("  \"description\": \"").append(description).append("\",\n");
        sb.append("  \"parameters\": {\n");
        sb.append("    \"type\": \"object\",\n");
        sb.append("    \"properties\": {\n");

        boolean first = true;
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            if (!first) sb.append(",\n");
            sb.append("      \"").append(param.getKey()).append("\": {\n");
            sb.append("        \"type\": \"").append(param.getValue()).append("\"\n");
            sb.append("      }");
            first = false;
        }

        sb.append("\n    }\n");
        sb.append("  }\n");
        sb.append("}\n");
        return sb.toString();
    }
}
