package com.bilibili.backend.service;

import com.bilibili.backend.entity.Like;
import com.bilibili.backend.mapper.LikeMapper;
import com.bilibili.backend.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Slf4j
public class LikeService {
    
    @Autowired
    private LikeMapper likeMapper;
    
    @Autowired
    private VideoMapper videoMapper;
    
    @Transactional
    public void likeVideo(Long userId, Long videoId) {
        log.info("用户{}点赞视频{}", userId, videoId);
        Like like = likeMapper.selectByUserIdAndVideoId(userId, videoId);
        if (like != null) {
            throw new RuntimeException("已经点赞过了");
        }
        like = new Like();
        like.setUserId(userId);
        like.setVideoId(videoId);
        like.setCreateTime(LocalDateTime.now().toString());
        likeMapper.insert(like);
        videoMapper.increaseLikes(videoId);
    }
    
    public void unlikeVideo(Long userId, Long videoId) {
        log.info("用户{}取消点赞视频{}", userId, videoId);
        try {
            Like like = likeMapper.selectByUserIdAndVideoId(userId, videoId);
            if (like == null) {
                throw new RuntimeException("还没有点赞过");
            }
            likeMapper.delete(userId, videoId);
            videoMapper.decreaseLikes(videoId);
        } catch (Exception e) {
            log.error("取消点赞视频失败", e);
            throw e;
        }
    }
    
    public boolean hasLiked(Long userId, Long videoId) {
        return likeMapper.selectByUserIdAndVideoId(userId, videoId) != null;
    }
    
    public int getLikeCount(Long videoId) {
        return likeMapper.countByVideoId(videoId);
    }
} 