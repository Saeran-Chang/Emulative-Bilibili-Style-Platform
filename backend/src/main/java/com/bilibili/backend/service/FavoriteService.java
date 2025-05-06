package com.bilibili.backend.service;

import com.bilibili.backend.entity.Video;
import java.util.List;

public interface FavoriteService {
    void addFavorite(Long userId, Long videoId);
    void removeFavorite(Long userId, Long videoId);
    List<Video> getFavoriteVideos(Long userId);
    boolean isFavorite(Long userId, Long videoId);
} 