package com.bilibili.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long videoId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 