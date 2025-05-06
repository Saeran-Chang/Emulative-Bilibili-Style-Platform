package com.bilibili.backend.service;

import com.bilibili.backend.entity.Collect;
import com.bilibili.backend.entity.Video;
import com.bilibili.backend.mapper.CollectMapper;
import com.bilibili.backend.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CollectService {
    
    @Autowired
    private CollectMapper collectMapper;
    
    @Autowired
    private VideoMapper videoMapper;
    
    @Transactional
    public void collectVideo(Long userId, Long videoId) {
        log.info("用户{}收藏视频{}", userId, videoId);
        Collect collect = collectMapper.selectByUserIdAndVideoId(userId, videoId);
        if (collect != null) {
            throw new RuntimeException("已经收藏过了");
        }
        collect = new Collect();
        collect.setUserId(userId);
        collect.setVideoId(videoId);
        collect.setCreateTime(LocalDateTime.now().toString());
        collectMapper.insert(collect);
        videoMapper.increaseCollects(videoId);
    }
    
    public void uncollectVideo(Long userId, Long videoId) {
        log.info("用户{}取消收藏视频{}", userId, videoId);
        try {
            Collect collect = collectMapper.selectByUserIdAndVideoId(userId, videoId);
            if (collect == null) {
                throw new RuntimeException("还没有收藏过");
            }
            collectMapper.delete(userId, videoId);
            videoMapper.decreaseCollects(videoId);
        } catch (Exception e) {
            log.error("取消收藏视频失败", e);
            throw e;
        }
    }
    
    public boolean hasCollected(Long userId, Long videoId) {
        return collectMapper.selectByUserIdAndVideoId(userId, videoId) != null;
    }
    
    public int getCollectCount(Long videoId) {
        return collectMapper.countByVideoId(videoId);
    }
    
    public List<Video> getUserFavorites(Long userId) {
        return collectMapper.selectVideosByUserId(userId);
    }
} 