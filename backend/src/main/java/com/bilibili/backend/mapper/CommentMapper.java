package com.bilibili.backend.mapper;

import com.bilibili.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectByVideoId(Long videoId);
    Comment selectById(Long id);
    int insert(Comment comment);
    int updateLikes(Long id);
    int delete(Long id);
} 