package com.arcengtr.tool.impl;

import com.arcengtr.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class SendRefundFormTool extends Tool {
    @Override
    public Object execute(Map<String, Object> arguments) throws Exception {
        String email = (String) arguments.get("email");

        Map<String, Object> result = new HashMap<>();
        result.put("formId", UUID.randomUUID().toString());
        result.put("status", "SENT");
        result.put("message", "Refund form has been sent to " + email);

        log.info("Refund form sent to: {}", email);
        return result;
    }
}