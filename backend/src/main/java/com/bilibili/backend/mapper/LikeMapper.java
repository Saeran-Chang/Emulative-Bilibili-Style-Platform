package com.bilibili.backend.mapper;

import com.bilibili.backend.entity.Like;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {
    Like selectByUserIdAndVideoId(Long userId, Long videoId);
    int insert(Like like);
    int delete(Long userId, Long videoId);
    int countByVideoId(Long videoId);
} 