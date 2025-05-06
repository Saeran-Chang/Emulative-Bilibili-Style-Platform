package com.bilibili.backend.entity;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private Long userId;
    private Long videoId;
    private String content;
    private Long likes;
    private String createTime;
} 