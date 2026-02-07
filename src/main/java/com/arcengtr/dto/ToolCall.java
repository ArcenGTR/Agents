package com.arcengtr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {
    private String id;
    private String type;
    private CallFunction function;
}
