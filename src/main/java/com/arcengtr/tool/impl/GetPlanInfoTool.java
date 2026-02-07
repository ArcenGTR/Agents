package com.arcengtr.tool.impl;

import com.arcengtr.tool.Tool;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GetPlanInfoTool extends Tool {
    @Override
    public Object execute(Map<String, Object> arguments) throws Exception {
        String customerId = (String) arguments.get("customerId");

        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("plan", "Professional");
        result.put("monthlyPrice", 99.99);
        result.put("status", "ACTIVE");
        result.put("purchaseDate", LocalDate.now().minusDays(10).format(DateTimeFormatter.ISO_DATE));
        result.put("nextBillingDate", LocalDate.now().plusDays(15).format(DateTimeFormatter.ISO_DATE));
        result.put("features", new String[]{"API Access", "Priority Support"});

        log.info("Plan info retrieved for customer: {}", customerId);
        return result;
    }
}