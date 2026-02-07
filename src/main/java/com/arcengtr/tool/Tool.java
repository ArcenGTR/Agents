package com.arcengtr.tool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Tool {
    protected String name;
    protected String description;
    protected Map<String, ParameterInfo> parameters;
    protected String implementationClass;

    @Data
    public static class ParameterInfo {
        private String type;
        private String description;
    }

    public abstract Object execute(Map<String, Object> arguments) throws Exception;

    public Tool(String name, String description) {
        this.name = name;
        this.description = description;
        this.parameters = new HashMap<>();
    }

    public Map<String, Object> toJsonSchemaMap() {
        Map<String, Object> properties = new HashMap<>();
        parameters.forEach((pName, pInfo) -> {
            properties.put(pName, Map.of(
                    "type", pInfo.getType(),
                    "description", pInfo.getDescription() != null ? pInfo.getDescription() : ""
            ));
        });

        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", name,
                        "description", description,
                        "parameters", Map.of(
                                "type", "object",
                                "properties", properties,
                                "required", parameters.keySet()
                        )
                )
        );
    }
}
