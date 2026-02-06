package com.arcengtr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {
    private String role; // "system", "user", "assistant"
    private String content;

    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    @JsonProperty("tool_call_id")
    private String toolCallId;

    public static ConversationMessage system(String content) {
        return ConversationMessage.builder()
                .role("system")
                .content(content)
                .build();
    }

    public static ConversationMessage user(String content) {
        return ConversationMessage.builder()
                .role("user")
                .content(content)
                .build();
    }

    public static ConversationMessage assistant(String content) {
        return ConversationMessage.builder()
                .role("assistant")
                .content(content)
                .build();
    }
}
