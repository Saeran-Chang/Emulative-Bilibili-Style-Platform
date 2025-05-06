package com.bilibili.backend.service;

import com.bilibili.backend.entity.Video;
import com.bilibili.backend.mapper.VideoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {
    
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    
    @Autowired
    private VideoMapper videoMapper;
    
    public List<Video> getAllVideos() {
        log.info("获取所有视频列表");
        try {
            return videoMapper.selectAll();
        } catch (Exception e) {
            log.error("获取视频列表失败", e);
            throw e;
        }
    }
    
    public Video getVideoById(Long id) {
        log.info("获取视频，ID：{}", id);
        try {
            return videoMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取视频失败", e);
            throw e;
        }
    }
    
    public void addVideo(Video video) {
        log.info("添加视频：{}", video);
        try {
            videoMapper.insert(video);
        } catch (Exception e) {
            log.error("添加视频失败", e);
            throw e;
        }
    }
    
    public void updateVideo(Video video) {
        log.info("更新视频：{}", video);
        try {
            videoMapper.update(video);
        } catch (Exception e) {
            log.error("更新视频失败", e);
            throw e;
        }
    }
    
    public void deleteVideo(Long id) {
        log.info("删除视频，ID：{}", id);
        try {
            videoMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除视频失败", e);
            throw e;
        }
    }
    
    public void increaseViews(Long videoId) {
        log.info("增加视频播放次数，ID：{}", videoId);
        try {
            videoMapper.increaseViews(videoId);
        } catch (Exception e) {
            log.error("增加播放次数失败", e);
            throw e;
        }
    }
    
    public void increaseShares(Long videoId) {
        log.info("增加视频分享次数，ID：{}", videoId);
        try {
            videoMapper.increaseShares(videoId);
        } catch (Exception e) {
            log.error("增加分享次数失败", e);
            throw e;
        }
    }
} 