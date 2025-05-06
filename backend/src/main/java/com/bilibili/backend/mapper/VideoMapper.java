package com.bilibili.backend.mapper;

import com.bilibili.backend.entity.Video;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoMapper {
    List<Video> selectAll();
    
    Video selectById(Long id);
    
    int insert(Video video);
    
    int update(Video video);
    
    int deleteById(Long id);
    
    int increaseLikes(Long videoId);
    
    int decreaseLikes(Long videoId);
    
    int increaseCollects(Long videoId);
    
    int decreaseCollects(Long videoId);
    
    int increaseViews(Long videoId);
    
    int increaseShares(Long videoId);
} 