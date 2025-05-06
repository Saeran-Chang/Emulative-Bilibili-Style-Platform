package com.bilibili.backend.controller;

import com.bilibili.backend.entity.*;
import com.bilibili.backend.service.*;
import com.bilibili.backend.common.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private static final Logger log = LoggerFactory.getLogger(VideoController.class);

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private VideoService videoService;
    
    @Autowired
    private LikeService likeService;
    
    @Autowired
    private CollectService collectService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtService jwtService;

    // 工具方法：获取视频时长（秒）
    private double getVideoDurationInSeconds(String videoFilePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "ffprobe", "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", videoFilePath
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = br.readLine();
                if (line != null) {
                    return Double.parseDouble(line);
                }
            }
        } catch (Exception e) {
            log.error("ffprobe 获取视频时长失败", e);
        }
        return 0;
    }

    // 工具方法：格式化为hh:mm:ss
    private String formatDuration(double seconds) {
        int totalSeconds = (int) Math.round(seconds);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        log.info("获取所有视频列表");
        try {
            List<Video> videos = videoService.getAllVideos();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            log.error("获取视频列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        log.info("获取视频，ID：{}", id);
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtService.getUserIdFromToken(actualToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Video video = videoService.getVideoById(id);
            if (video == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查用户是否已经点赞和收藏
            video.setIsLiked(likeService.hasLiked(userId, id));
            video.setIsCollected(collectService.hasCollected(userId, id));

            // 获取实际的收藏总数
            int collectCount = collectService.getCollectCount(id);
            log.info("视频ID：{}的收藏总数为：{}", id, collectCount);
            video.setCollects(collectCount);

            return ResponseEntity.ok(video);
        } catch (Exception e) {
            log.error("获取视频失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除视频失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("cover") MultipartFile cover) {
        log.info("接收到上传视频的请求，标题：{}", title);
        try {
            // 确保上传目录存在
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 保存视频文件
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // 获取视频时长
            double durationSeconds = getVideoDurationInSeconds(filePath.toString());
            log.info("ffprobe 获取到的视频时长（秒）: {}", durationSeconds);
            String durationStr = formatDuration(durationSeconds);
            log.info("格式化后时长: {}", durationStr);

            // 保存封面图片
            String coverFileName = UUID.randomUUID().toString() + ".jpg";
            Path coverPath = uploadDir.resolve(coverFileName);
            Files.copy(cover.getInputStream(), coverPath);

            // 创建视频记录
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setFilePath(fileName);
            video.setCoverPath(coverFileName);
            video.setAuthorId(1L); // 这里应该从当前登录用户获取
            video.setDuration(durationStr);
            video.setViews(0);
            video.setLikes(0);
            video.setCollects(0);
            video.setShares(0);
            video.setStatus(1);

            // 保存视频信息到数据库
            videoService.addVideo(video);
            log.info("成功上传视频，ID：{}", video.getId());

            return ResponseEntity.ok().body("视频上传成功");
        } catch (IOException e) {
            log.error("视频上传失败", e);
            return ResponseEntity.badRequest().body("视频上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/stream/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String filename) {
        log.info("接收到视频流请求，文件名：{}", filename);
        try {
            Path videoPath = Paths.get(uploadPath, filename);
            Resource videoResource = new UrlResource(videoPath.toUri());
            
            if (videoResource.exists() && videoResource.isReadable()) {
                log.info("成功返回视频流，文件名：{}", filename);
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(videoResource);
            } else {
                log.warn("视频文件不存在或不可读，文件名：{}", filename);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取视频流失败，文件名：{}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody Video video) {
        log.info("接收到更新视频的请求，ID：{}", id);
        try {
            video.setId(id);
            videoService.updateVideo(video);
            Video updatedVideo = videoService.getVideoById(id);
            return ResponseEntity.ok(updatedVideo);
        } catch (Exception e) {
            log.error("更新视频失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeVideo(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtService.getUserIdFromToken(actualToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            boolean isLiked = likeService.hasLiked(userId, id);
            if (isLiked) {
                likeService.unlikeVideo(userId, id);
                return ResponseEntity.ok(new ActionResponse(true, "取消点赞成功"));
            } else {
                likeService.likeVideo(userId, id);
                return ResponseEntity.ok(new ActionResponse(true, "点赞成功"));
            }
        } catch (RuntimeException e) {
            log.error("点赞操作失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ActionResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("点赞操作失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ActionResponse(false, "服务器错误"));
        }
    }

    @PostMapping("/{id}/collect")
    public ResponseEntity<?> collectVideo(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtService.getUserIdFromToken(actualToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            boolean isCollected = collectService.hasCollected(userId, id);
            if (isCollected) {
                collectService.uncollectVideo(userId, id);
                return ResponseEntity.ok(new ActionResponse(true, "取消收藏成功"));
            } else {
                collectService.collectVideo(userId, id);
                return ResponseEntity.ok(new ActionResponse(true, "收藏成功"));
            }
        } catch (RuntimeException e) {
            log.error("收藏操作失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ActionResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("收藏操作失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ActionResponse(false, "服务器错误"));
        }
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Void> shareVideo(@PathVariable Long id) {
        log.info("分享视频，ID：{}", id);
        try {
            videoService.increaseShares(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("分享视频失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long id) {
        try {
            List<Comment> comments = commentService.getCommentsByVideoId(id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("获取评论失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody Comment comment, @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtService.getUserIdFromToken(actualToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            comment.setVideoId(id);
            comment.setUserId(userId);
            comment.setLikes(0L);
            commentService.addComment(comment);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("添加评论失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId, @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtService.getUserIdFromToken(actualToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            commentService.likeComment(commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("点赞评论失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> increaseViews(@PathVariable Long id) {
        log.info("增加视频播放次数，ID：{}", id);
        try {
            videoService.increaseViews(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("增加播放次数失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

// 添加错误响应类
class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 