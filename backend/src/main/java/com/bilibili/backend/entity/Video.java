package com.bilibili.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    private Long id;
    private String title;
    private String description;
    private String filePath;
    private String coverPath;
    private Long authorId;
    private String duration;
    private Integer views;
    private Integer status;
    private String createTime;
    private String updateTime;
    private Integer likes;
    private Integer collects;
    private Integer shares;
    
    @Transient
    private Boolean isLiked;
    
    @Transient
    private Boolean isCollected;
} 