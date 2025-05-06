package com.bilibili.backend.entity;

import lombok.Data;

@Data
public class Like {
    private Long id;
    private Long userId;
    private Long videoId;
    private String createTime;
} 