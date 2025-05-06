package com.bilibili.backend.controller;

import com.bilibili.backend.dto.LoginRequest;
import com.bilibili.backend.dto.LoginResponse;
import com.bilibili.backend.entity.User;
import com.bilibili.backend.entity.Video;
import com.bilibili.backend.mapper.UserMapper;
import com.bilibili.backend.service.JwtService;
import com.bilibili.backend.service.CollectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private CollectService collectService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        LoginResponse response = new LoginResponse();
        response.setToken("dummy-token-" + user.getId()); // 这里应该使用JWT生成真实token

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        response.setUserInfo(userInfo);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userMapper.findById(id);
    }

    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userMapper.selectByUsername(username);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        // TODO: 实现创建用户
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Video>> getFavorites(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtService.getUserIdFromToken(actualToken);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            List<Video> favorites = collectService.getUserFavorites(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            log.error("获取用户收藏列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(@RequestAttribute Long userId) {
        try {
            User user = userMapper.findById(userId);
            if (user != null) {
                // 清除敏感信息
                user.setPassword(null);
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 