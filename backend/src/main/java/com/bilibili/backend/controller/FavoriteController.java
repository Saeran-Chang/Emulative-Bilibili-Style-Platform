package com.bilibili.backend.controller;

import com.bilibili.backend.common.Result;
import com.bilibili.backend.entity.Video;
import com.bilibili.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/{videoId}")
    public Result<Void> addFavorite(@RequestAttribute Long userId, @PathVariable Long videoId) {
        favoriteService.addFavorite(userId, videoId);
        return Result.success();
    }

    @DeleteMapping("/{videoId}")
    public Result<Void> removeFavorite(@RequestAttribute Long userId, @PathVariable Long videoId) {
        favoriteService.removeFavorite(userId, videoId);
        return Result.success();
    }

    @GetMapping
    public Result<List<Video>> getFavoriteVideos(@RequestAttribute Long userId) {
        List<Video> videos = favoriteService.getFavoriteVideos(userId);
        return Result.success(videos);
    }

    @GetMapping("/{videoId}/status")
    public Result<Boolean> isFavorite(@RequestAttribute Long userId, @PathVariable Long videoId) {
        boolean status = favoriteService.isFavorite(userId, videoId);
        return Result.success(status);
    }
} 