<template>
  <div class="video-list">
    <div class="header">
      <div class="left">
        <h1>视频列表</h1>
      </div>
      <div class="right">
        <el-button type="primary" @click="$router.push('/upload')" class="upload-btn">
          <el-icon><Upload /></el-icon>
          上传视频
        </el-button>
      </div>
    </div>
    
    <div class="video-grid">
      <div v-for="video in videos" :key="video.id" class="video-card" @click="handlePlay(video)">
        <div class="video-cover">
          <img :src="`/api/videos/stream/${video.coverPath}`" :alt="video.title">
          <div class="video-duration">{{ video.duration }}</div>
          <div class="video-views">
            <el-icon><View /></el-icon>
            {{ formatViews(video.views) }}
          </div>
        </div>
        <div class="video-info">
          <h3 class="video-title">{{ video.title }}</h3>
          <p class="video-desc">{{ video.description }}</p>
        </div>
        <div class="video-actions">
          <el-button type="primary" size="small" @click.stop="handleEdit(video)">编辑</el-button>
          <el-button type="danger" size="small" @click.stop="handleDelete(video)">删除</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, View } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const videos = ref([])

const fetchVideos = async () => {
  try {
    const response = await axios.get('/api/videos')
    videos.value = response.data
  } catch (error) {
    ElMessage.error('获取视频列表失败')
  }
}

const handlePlay = (video) => {
  router.push(`/video/${video.id}`)
}

const handleDelete = async (video) => {
  try {
    await ElMessageBox.confirm('确定要删除这个视频吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`/api/videos/${video.id}`)
    ElMessage.success('删除成功')
    fetchVideos()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleEdit = (video) => {
  router.push(`/edit/${video.id}`)
}

const formatViews = (views) => {
  if (views >= 10000) {
    return (views / 10000).toFixed(1) + '万'
  }
  return views
}

onMounted(() => {
  fetchVideos()
})
</script>

<style scoped>
.video-list {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 10px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  background: rgba(0, 0, 0, 0.3);
  padding: 8px 16px;
  border-radius: 4px;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 8px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 10px;
}

.video-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  cursor: pointer;
}

.video-card:hover {
  transform: translateY(-5px);
}

.video-cover {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 宽高比 */
  background-color: #f4f5f7;
}

.video-cover img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-views {
  position: absolute;
  bottom: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.video-info {
  padding: 12px;
}

.video-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: var(--text-color);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.video-desc {
  margin: 0;
  font-size: 14px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.video-actions {
  padding: 12px;
  display: flex;
  gap: 8px;
  border-top: 1px solid #eee;
}
</style> 