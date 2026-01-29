package com.arcengtr.tool;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class BillingTools {
    public static class ProcessRefundTool extends Tool {
        public ProcessRefundTool() {
            super("process_refund", "Process a refund for a customer based on their account and refund policy");
            addParameter("customerId", "string", "The customer ID");
            addParameter("amount", "number", "The refund amount");
            addParameter("reason", "string", "The reason for refund");
        }

        @Override
        public Object execute(Map<String, Object> arguments) throws Exception {
            String customerId = (String) arguments.get("customerId");
            Number amount = (Number) arguments.get("amount");
            String reason = (String) arguments.get("reason");

            // Simulate refund processing
            String refundId = UUID.randomUUID().toString();
            LocalDate processingDate = LocalDate.now().plusDays(5);

            Map<String, Object> result = new HashMap<>();
            result.put("refundId", refundId);
            result.put("customerId", customerId);
            result.put("amount", amount.doubleValue());
            result.put("reason", reason);
            result.put("status", "PENDING");
            result.put("estimatedProcessingDate", processingDate.format(DateTimeFormatter.ISO_DATE));
            result.put("message", "Refund request processed. You will receive funds within 5-7 business days.");

            log.info("Refund processed: {} for customer: {}", refundId, customerId);
            return result;
        }
    }

    public static class SendRefundFormTool extends Tool {
        public SendRefundFormTool() {
            super("send_refund_form", "Send a refund request form to the customer");
            addParameter("customerId", "string", "The customer ID");
            addParameter("email", "string", "Customer email address");
        }

        @Override
        public Object execute(Map<String, Object> arguments) throws Exception {
            String customerId = (String) arguments.get("customerId");
            String email = (String) arguments.get("email");

            Map<String, Object> result = new HashMap<>();
            result.put("formId", UUID.randomUUID().toString());
            result.put("customerId", customerId);
            result.put("email", email);
            result.put("status", "SENT");
            result.put("expiresAt", LocalDate.now().plusDays(14).format(DateTimeFormatter.ISO_DATE));
            result.put("message", "Refund form has been sent to " + email + ". Please fill it out and submit within 14 days.");

            log.info("Refund form sent to: {}", email);
            return result;
        }
    }

    public static class GetPlanInfoTool extends Tool {
        public GetPlanInfoTool() {
            super("get_plan_info", "Retrieve customer's current plan and pricing information");
            addParameter("customerId", "string", "The customer ID");
        }

        @Override
        public Object execute(Map<String, Object> arguments) throws Exception {
            String customerId = (String) arguments.get("customerId");

            // Simulated plan data
            Map<String, Object> result = new HashMap<>();
            result.put("customerId", customerId);
            result.put("plan", "Professional");
            result.put("monthlyPrice", 99.99);
            result.put("annualPrice", 999.90);
            result.put("billingCycle", "monthly");
            result.put("nextBillingDate", LocalDate.now().plusDays(15).format(DateTimeFormatter.ISO_DATE));
            result.put("features", new String[]{"API Access", "Priority Support", "Custom Integration"});
            result.put("status", "ACTIVE");

            log.info("Plan info retrieved for customer: {}", customerId);
            return result;
        }
    }

    public static class OpenSupportCaseTool extends Tool {
        public OpenSupportCaseTool() {
            super("open_support_case", "Open a billing support case for further investigation");
            addParameter("customerId", "string", "The customer ID");
            addParameter("issueType", "string", "Type of issue");
            addParameter("description", "string", "Detailed description of the issue");
        }

        @Override
        public Object execute(Map<String, Object> arguments) throws Exception {
            String customerId = (String) arguments.get("customerId");
            String issueType = (String) arguments.get("issueType");
            String description = (String) arguments.get("description");

            String caseId = "CASE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Map<String, Object> result = new HashMap<>();
            result.put("caseId", caseId);
            result.put("customerId", customerId);
            result.put("issueType", issueType);
            result.put("status", "OPEN");
            result.put("priority", "HIGH");
            result.put("createdDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            result.put("expectedResolution", LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_DATE));
            result.put("message", "Support case " + caseId + " has been opened. Our team will review your case shortly.");

            log.info("Support case opened: {} for customer: {}", caseId, customerId);
            return result;
        }
    }
}
