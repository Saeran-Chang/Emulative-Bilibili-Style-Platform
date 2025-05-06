package com.bilibili.backend.service.impl;

import com.bilibili.backend.entity.Favorite;
import com.bilibili.backend.entity.Video;
import com.bilibili.backend.mapper.FavoriteMapper;
import com.bilibili.backend.mapper.VideoMapper;
import com.bilibili.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long videoId) {
        if (!isFavorite(userId, videoId)) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setVideoId(videoId);
            favoriteMapper.insert(favorite);
        }
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long videoId) {
        if (isFavorite(userId, videoId)) {
            favoriteMapper.delete(userId, videoId);
            // 更新视频的收藏数
            videoMapper.decreaseCollects(videoId);
        }
    }

    @Override
    public List<Video> getFavoriteVideos(Long userId) {
        List<Favorite> favorites = favoriteMapper.findByUserId(userId);
        return favorites.stream()
                .map(favorite -> {
                    Video video = videoMapper.selectById(favorite.getVideoId());
                    if (video != null) {
                        video.setIsCollected(true);
                    }
                    return video;
                })
                .filter(video -> video != null)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorite(Long userId, Long videoId) {
        return favoriteMapper.exists(userId, videoId) > 0;
    }
} 