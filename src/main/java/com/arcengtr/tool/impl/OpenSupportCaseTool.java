package com.arcengtr.tool.impl;

import com.arcengtr.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class OpenSupportCaseTool extends Tool {
    @Override
    public Object execute(Map<String, Object> arguments) throws Exception {
        String caseId = "CASE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Map<String, Object> result = new HashMap<>();
        result.put("caseId", caseId);
        result.put("status", "OPEN");
        result.put("expectedResolution", LocalDate.now().plusDays(2).toString());

        log.info("Support case opened: {}", caseId);
        return result;
    }
}
