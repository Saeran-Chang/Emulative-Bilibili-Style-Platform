# 仿Bilibili 视频平台项目

## 🚀 项目简介

本项目是一个仿Bilibili的视频分享平台，包含前后端分离架构，支持用户登录、视频上传、播放、评论、点赞、收藏等功能。

- **前端**：基于 Vue 3 + TypeScript + Element Plus，使用 Vite 构建。
- **后端**：基于 Spring Boot 3，集成 MyBatis、Spring Security、JWT、Redis、MySQL 等。
- **存储**：视频及封面图片存储于 `uploads/` 目录。

---

## 📁 目录结构

```
Bilibili/
├── frontend/   # 前端项目（Vue 3）
├── backend/    # 后端项目（Spring Boot）
├── uploads/    # 视频与图片上传目录
```

---

## 🎯 主要功能

### 前端
- 用户登录
- 视频上传、封面上传
- 视频播放、评论、点赞、收藏
- 视频列表、个人中心、收藏夹

### 后端
- 用户管理与认证（JWT）
- 视频管理（上传、播放、统计）
- 评论、点赞、收藏等社交功能
- 数据持久化（MySQL）

---

## 🚀 快速开始

### 1. 启动后端

```bash
cd backend
# 配置 application.yml 数据库等信息
mvn spring-boot:run
```

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 3. 访问

- 前端开发环境默认：http://localhost:5173
- 后端接口默认：http://localhost:8080

---

## 🔧 依赖环境

- Node.js 16+
- JDK 21+
- MySQL 8+
- Redis

---

## 📦 其他说明

- 视频与图片文件上传至 `uploads/` 目录


## 📄 许可证
本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📮 联系我们
- 项目维护者：[SaeranChang]
- 邮箱：[SaeranChang@proton.me]

