package com.bilibili.backend.service;

import com.bilibili.backend.entity.Comment;
import com.bilibili.backend.mapper.CommentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Transactional
    public void addComment(Comment comment) {
        log.info("添加评论：{}", comment);
        try {
            commentMapper.insert(comment);
        } catch (Exception e) {
            log.error("添加评论失败", e);
            throw e;
        }
    }
    
    public List<Comment> getCommentsByVideoId(Long videoId) {
        log.info("获取视频{}的评论", videoId);
        try {
            return commentMapper.selectByVideoId(videoId);
        } catch (Exception e) {
            log.error("获取评论失败", e);
            throw e;
        }
    }
    
    @Transactional
    public void likeComment(Long commentId) {
        log.info("点赞评论，ID：{}", commentId);
        try {
            commentMapper.updateLikes(commentId);
        } catch (Exception e) {
            log.error("点赞评论失败", e);
            throw e;
        }
    }
    
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("删除评论，ID：{}", commentId);
        try {
            commentMapper.delete(commentId);
        } catch (Exception e) {
            log.error("删除评论失败", e);
            throw e;
        }
    }
} 