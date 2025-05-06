<template>
  <div class="favorites-container">
    <h1 class="page-title">我的收藏</h1>
    <div class="video-grid">
      <div v-for="video in favoriteVideos" :key="video.id" class="video-card" @click="goToVideo(video.id)">
        <div class="video-thumbnail">
          <img :src="`/api/videos/stream/${video.coverPath}`" :alt="video.title">
          <span class="duration">{{ video.duration }}</span>
        </div>
        <div class="video-info">
          <h3 class="video-title">{{ video.title }}</h3>
          <div class="video-stats">
            <span class="views">
              <el-icon><View /></el-icon>
              {{ formatNumber(video.views) }}
            </span>
            <span class="likes">
              <el-icon><Star /></el-icon>
              {{ formatNumber(video.likes) }}
            </span>
            <span class="collects">
              <el-icon><Collection /></el-icon>
              {{ formatNumber(video.collects) }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="favoriteVideos.length === 0" class="empty-state">
      <el-empty description="暂无收藏视频" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, Star, Collection } from '@element-plus/icons-vue'
import request from '../utils/axios'

const router = useRouter()
const favoriteVideos = ref([])

const formatNumber = (num) => {
  if (!num) return '0'
  if (num < 1000) return num.toString()
  if (num < 10000) return (num / 1000).toFixed(1) + 'K'
  return (num / 10000).toFixed(1) + 'W'
}

const goToVideo = (videoId) => {
  router.push(`/video/${videoId}`)
}

const fetchFavorites = async () => {
  try {
    const response = await request.get('/api/favorites');
    favoriteVideos.value = response.data.data;
  } catch (error) {
    ElMessage.error('获取收藏列表失败');
  }
}

onMounted(() => {
  fetchFavorites();
})
</script>

<style scoped>
.favorites-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  background: rgba(0, 0, 0, 0.3);
  padding: 8px 16px;
  border-radius: 4px;
  display: inline-block;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.video-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.video-card:hover {
  transform: translateY(-4px);
}

.video-thumbnail {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 aspect ratio */
}

.video-thumbnail img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-info {
  padding: 12px;
}

.video-title {
  font-size: 16px;
  margin: 0 0 8px;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.video-stats {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 14px;
}

.video-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-state {
  margin-top: 40px;
  text-align: center;
}
</style> 