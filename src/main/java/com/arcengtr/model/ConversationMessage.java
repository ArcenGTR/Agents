package com.arcengtr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {
    private String role; // "system", "user", "assistant"
    private String content;

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
