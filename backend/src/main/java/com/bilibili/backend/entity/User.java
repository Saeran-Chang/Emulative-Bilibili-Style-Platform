package com.bilibili.backend.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private Integer status;
    private String createTime;
    private String updateTime;
} 