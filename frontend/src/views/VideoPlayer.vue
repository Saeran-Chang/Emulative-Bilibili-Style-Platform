<template>
  <div class="video-player">
    <div class="video-container">
      <div class="video-wrapper">
        <video
          ref="videoPlayer"
          :src="videoUrl"
          controls
          class="video-element"
          @play="handlePlay"
          @pause="handlePause"
          @loadeddata="handleVideoLoad"
          preload="auto"
        >
          <source :src="videoUrl" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
        <div class="danmaku-container"></div>
      </div>
    </div>
    <div class="video-info">
      <div class="video-header">
        <h1>{{ video.title }}</h1>
        <div class="video-actions">
          <el-button 
            :type="video.isLiked ? 'success' : 'primary'" 
            size="small" 
            @click="handleLike"
          >
            <el-icon><Star /></el-icon>
            {{ video.isLiked ? '已点赞' : '点赞' }}
            <span class="count" v-if="video.likes">({{ formatNumber(video.likes) }})</span>
          </el-button>
          <el-button 
            :type="video.isCollected ? 'success' : 'primary'" 
            size="small" 
            @click="handleCollect"
          >
            <el-icon><Collection /></el-icon>
            {{ video.isCollected ? '已收藏' : '收藏' }}
            <span class="count" v-if="video.collects">({{ formatNumber(video.collects) }})</span>
          </el-button>
          <el-button type="primary" size="small" @click="handleShare">
            <el-icon><Share /></el-icon>
            分享
          </el-button>
        </div>
      </div>
      <div class="video-stats">
        <span class="stat-item">
          <el-icon><View /></el-icon>
          {{ formatNumber(video.views) }} 播放
        </span>
        <span class="stat-item">
          <el-icon><Clock /></el-icon>
          {{ formatDate(video.createTime) }}
        </span>
      </div>
      <div class="video-description">
        <p>{{ video.description }}</p>
      </div>
    </div>
    
    <div class="comment-section">
      <div class="comment-header">
        <h2>评论区</h2>
        <span class="comment-count">{{ comments.length }} 条评论</span>
      </div>
      
      <div class="comment-input">
        <el-avatar :size="40" :src="userAvatar" />
        <div class="input-wrapper">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="发个友善的评论吧~"
            maxlength="200"
            show-word-limit
          />
          <div class="input-actions">
            <el-button type="primary" size="small" @click="submitComment">
              发表评论
            </el-button>
          </div>
        </div>
      </div>

      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <el-avatar :size="40" :src="comment.userAvatar" />
          <div class="comment-content">
            <div class="comment-header">
              <span class="username">{{ comment.username }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
            <div class="comment-actions">
              <span class="action-item" @click="handleLikeComment(comment)">
                <el-icon><Star /></el-icon>
                {{ comment.likes }}
              </span>
              <span class="action-item" @click="handleReply(comment)">
                <el-icon><ChatDotRound /></el-icon>
                回复
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Collection, Share, View, Clock, ChatDotRound } from '@element-plus/icons-vue'
import request from '../utils/axios'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const videoPlayer = ref(null)
const video = ref({})
const videoUrl = ref('')
const localLikeState = ref(false)
const localCollectState = ref(false)

const comments = ref([])
const commentContent = ref('')
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png') // 默认头像

const fetchVideo = async () => {
  try {
    const [videoResponse, favoriteResponse] = await Promise.all([
      request.get(`/api/videos/${route.params.id}`),
      request.get(`/api/favorites/${route.params.id}/status`)
    ]);
    
    // 设置视频信息，包括收藏数
    video.value = {
      ...videoResponse.data,
      isCollected: favoriteResponse.data.data
    };
    
    videoUrl.value = `/api/videos/stream/${video.value.filePath}`;
    
    videoPlayer.value.onerror = () => {
      ElMessage.error('视频加载失败，请稍后重试');
    };
  } catch (error) {
    ElMessage.error('获取视频信息失败');
  }
};

const handlePlay = () => {
  // 可以在这里添加播放统计逻辑
}

const handlePause = () => {
  // 可以在这里添加暂停处理逻辑
}

const handleVideoLoad = () => {
  if (videoPlayer.value) {
    videoPlayer.value.play().catch(error => {
      console.log('视频自动播放失败:', error)
    })
    
    // 增加播放次数
    request.post(`/api/videos/${route.params.id}/view`)
      .catch(error => {
        console.error('增加播放次数失败:', error)
      })
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const handleLike = async () => {
  try {
    const response = await request.post(`/api/videos/${video.value.id}/like`);
    if (response.data.success) {
      video.value.isLiked = !video.value.isLiked;
      video.value.likes = video.value.isLiked ? 
        (video.value.likes || 0) + 1 : 
        Math.max((video.value.likes || 1) - 1, 0);
      ElMessage.success(response.data.message);
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败');
  }
};

const handleCollect = async () => {
  try {
    if (video.value.isCollected) {
      await request.delete(`/api/favorites/${video.value.id}`);
      video.value.isCollected = false;
      // 重新获取视频信息以更新收藏数
      const videoResponse = await request.get(`/api/videos/${route.params.id}`);
      video.value.collects = videoResponse.data.collects;
      ElMessage.success('已取消收藏');
    } else {
      await request.post(`/api/favorites/${video.value.id}`);
      video.value.isCollected = true;
      // 重新获取视频信息以更新收藏数
      const videoResponse = await request.get(`/api/videos/${route.params.id}`);
      video.value.collects = videoResponse.data.collects;
      ElMessage.success('收藏成功');
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败');
  }
};

const handleShare = async () => {
  try {
    // 获取当前视频的完整URL
    const videoUrl = window.location.href
    
    // 复制链接到剪贴板
    await navigator.clipboard.writeText(videoUrl)
    
    // 调用分享API
    await request.post(`/api/videos/${video.value.id}/share`)
    video.value.shares = (video.value.shares || 0) + 1
    
    ElMessage.success('链接已复制到剪贴板')
  } catch (error) {
    ElMessage.error('分享失败')
  }
}

const fetchComments = async () => {
  try {
    const response = await request.get(`/api/videos/${route.params.id}/comments`)
    comments.value = response.data
  } catch (error) {
    ElMessage.error('获取评论失败')
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await request.post(`/api/videos/${video.value.id}/comments`, {
      content: commentContent.value
    })

    ElMessage.success('评论发表成功')
    commentContent.value = ''
    fetchComments()
  } catch (error) {
    ElMessage.error('评论发表失败')
  }
}

const handleLikeComment = async (comment) => {
  try {
    await request.post(`/api/comments/${comment.id}/like`)
    comment.likes += 1
  } catch (error) {
    if (error.response?.status === 400) {
      ElMessage.warning(error.response.data.message)
    }
  }
}

const handleReply = (comment) => {
  commentContent.value = `@${comment.username} `
}

const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) { // 1分钟内
    return '刚刚'
  } else if (diff < 3600000) { // 1小时内
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) { // 1天内
    return `${Math.floor(diff / 3600000)}小时前`
  } else {
    return date.toLocaleDateString('zh-CN', {
      month: 'long',
      day: 'numeric'
    })
  }
}

onMounted(() => {
  fetchVideo()
  fetchComments()
})
</script>

<style scoped>
.video-player {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background-color: #f4f4f4;
}

.video-container {
  position: relative;
  width: 100%;
  padding-top: 56.25%;
  margin-bottom: 20px;
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.video-element {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.danmaku-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.video-info {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.video-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.video-header h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #18191c;
  font-weight: 500;
}

.video-actions {
  display: flex;
  gap: 10px;
}

.video-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #999;
  font-size: 0.9rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.video-description {
  color: #666;
  line-height: 1.6;
  font-size: 0.95rem;
}

:deep(.el-button) {
  display: flex;
  align-items: center;
  gap: 5px;
}

:deep(.el-button--primary) {
  background-color: #fb7299;
  border-color: #fb7299;
}

:deep(.el-button--primary:hover) {
  background-color: #ff85ad;
  border-color: #ff85ad;
}

.comment-section {
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.comment-header h2 {
  margin: 0;
  font-size: 1.2rem;
  color: #18191c;
}

.comment-count {
  margin-left: 10px;
  color: #999;
  font-size: 0.9rem;
}

.comment-input {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.input-wrapper {
  flex: 1;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
}

.comment-content {
  flex: 1;
}

.comment-content .comment-header {
  margin-bottom: 5px;
}

.username {
  font-weight: 500;
  color: #18191c;
}

.time {
  margin-left: 10px;
  color: #999;
  font-size: 0.9rem;
}

.comment-text {
  margin: 5px 0;
  color: #666;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 15px;
  color: #999;
  font-size: 0.9rem;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.action-item:hover {
  color: #fb7299;
}

:deep(.el-textarea__inner) {
  resize: none;
  border-radius: 4px;
}

:deep(.el-textarea__inner:focus) {
  border-color: #fb7299;
}

.video-actions .count {
  margin-left: 4px;
  font-size: 12px;
  opacity: 0.8;
}

.el-button.el-button--success {
  background-color: #67c23a;
  border-color: #67c23a;
}

.el-button.el-button--success:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}
</style> 