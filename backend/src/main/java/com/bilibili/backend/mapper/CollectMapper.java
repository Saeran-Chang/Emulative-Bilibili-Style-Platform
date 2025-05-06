package com.bilibili.backend.mapper;

import com.bilibili.backend.entity.Collect;
import com.bilibili.backend.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CollectMapper {
    Collect selectByUserIdAndVideoId(Long userId, Long videoId);
    int insert(Collect collect);
    int delete(Long userId, Long videoId);
    int countByVideoId(Long videoId);
    List<Video> selectVideosByUserId(Long userId);
} 