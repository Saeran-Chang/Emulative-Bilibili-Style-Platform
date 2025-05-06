package com.bilibili.backend.mapper;

import com.bilibili.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);
    User selectByUsername(String username);
    List<User> findAll();
} 