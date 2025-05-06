package com.bilibili.backend.entity;

import lombok.Data;

@Data
public class Collect {
    private Long id;
    private Long userId;
    private Long videoId;
    private String createTime;
} 