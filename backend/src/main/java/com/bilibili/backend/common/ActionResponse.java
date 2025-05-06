package com.bilibili.backend.common;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponse {
    private boolean success;
    private String message;
    private Object data;

    public ActionResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = success;
    }
} 