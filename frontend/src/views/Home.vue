<template>
  <div class="home">
    <div class="welcome-section">
      <h2>欢迎使用Bilibili</h2>
      <p>在这里您可以观看精彩视频</p>
    </div>
    
    <!-- 视频上传按钮 -->
    <div class="upload-section">
      <el-button type="primary" @click="showUploadDialog = true">
        <el-icon><Upload /></el-icon>
        上传视频
      </el-button>
    </div>

    <!-- 视频上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传视频"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="视频标题" required>
          <el-input v-model="uploadForm.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="视频描述">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入视频描述"
          />
        </el-form-item>
        <el-form-item label="视频文件" required>
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleVideoChange"
            :limit="1"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将视频文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 mp4、mov 等视频格式，文件大小不超过 2GB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="封面图片" required>
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleCoverChange"
            :limit="1"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将封面图片拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 jpg、png 等图片格式，建议尺寸 16:9
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUploadDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUpload" :loading="uploading">
            上传
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 视频播放器 -->
    <div class="video-player-section">
      <el-card class="video-player-card" shadow="hover">
        <template #header>
          <div class="player-header">
            <h3>视频播放</h3>
          </div>
        </template>
        <div class="video-container">
          <video 
            v-if="currentVideo" 
            :src="getVideoUrl(currentVideo.filePath)" 
            controls 
            class="video-player"
            @play="handleVideoPlay"
            @pause="handleVideoPause"
          ></video>
          <div v-else class="no-video">
            <el-empty description="请选择一个视频播放" />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 视频列表 -->
    <el-card class="video-list-card" shadow="hover">
      <template #header>
        <div class="table-header">
          <h3>视频列表</h3>
          <el-button type="primary" @click="refreshVideos" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <el-table 
        :data="videos" 
        style="width: 100%" 
        :stripe="true"
        :border="true"
        v-loading="loading"
        @row-click="handleVideoSelect"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="authorId" label="作者ID" width="100" />
        <el-table-column prop="duration" label="时长" width="100" />
        <el-table-column prop="views" label="播放量" width="100" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click.stop="playVideo(scope.row)"
              class="play-button"
            >
              <el-icon><VideoPlay /></el-icon>
              播放
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Refresh, VideoPlay, Upload, UploadFilled } from '@element-plus/icons-vue'

interface Video {
  id: number
  title: string
  filePath: string
  authorId: number
  duration: string
  views: number
}

interface UploadForm {
  title: string
  description: string
  videoFile: File | null
  coverFile: File | null
}

const router = useRouter()
const videos = ref<Video[]>([])
const currentVideo = ref<Video | null>(null)
const loading = ref(false)
const showUploadDialog = ref(false)
const uploading = ref(false)
const uploadForm = ref<UploadForm>({
  title: '',
  description: '',
  videoFile: null,
  coverFile: null
})

const getVideoUrl = (filePath: string) => {
  return `http://localhost:8080/uploads/${filePath}`
}

const fetchVideos = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const response = await axios.get('/api/videos', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    videos.value = response.data
  } catch (error) {
    console.error('获取视频列表失败:', error)
    ElMessage.error('获取视频列表失败')
  }
}

const refreshVideos = async () => {
  loading.value = true
  await fetchVideos()
  loading.value = false
}

const playVideo = (video: Video) => {
  currentVideo.value = video
}

const handleVideoSelect = (row: Video) => {
  playVideo(row)
}

const handleVideoPlay = () => {
  console.log('视频开始播放')
}

const handleVideoPause = () => {
  console.log('视频暂停播放')
}

const handleVideoChange = (file: any) => {
  uploadForm.value.videoFile = file.raw
}

const handleCoverChange = (file: any) => {
  uploadForm.value.coverFile = file.raw
}

const handleUpload = async () => {
  if (!uploadForm.value.title) {
    ElMessage.warning('请输入视频标题')
    return
  }
  if (!uploadForm.value.videoFile) {
    ElMessage.warning('请选择视频文件')
    return
  }
  if (!uploadForm.value.coverFile) {
    ElMessage.warning('请选择封面图片')
    return
  }

  const formData = new FormData()
  formData.append('title', uploadForm.value.title)
  formData.append('description', uploadForm.value.description)
  formData.append('file', uploadForm.value.videoFile)
  formData.append('cover', uploadForm.value.coverFile)

  uploading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    await axios.post('/api/videos/upload', formData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      }
    })

    ElMessage.success('视频上传成功')
    showUploadDialog.value = false
    refreshVideos()
  } catch (error) {
    console.error('视频上传失败:', error)
    ElMessage.error('视频上传失败')
  } finally {
    uploading.value = false
  }
}

onMounted(() => {
  fetchVideos()
})
</script>

<style scoped>
.home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-section h2 {
  font-size: 2rem;
  color: var(--text-color);
  margin-bottom: 10px;
}

.welcome-section p {
  font-size: 1.1rem;
  color: #666;
}

.upload-section {
  margin-bottom: 20px;
  text-align: right;
}

.video-player-section {
  margin-bottom: 40px;
}

.video-player-card {
  border-radius: 8px;
  overflow: hidden;
}

.player-header {
  text-align: center;
}

.player-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: var(--text-color);
}

.video-container {
  width: 100%;
  aspect-ratio: 16/9;
  background-color: #000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.no-video {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-list-card {
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: var(--text-color);
}

.play-button {
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background-color: #f5f7fa;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
  cursor: pointer;
}

:deep(.el-button) {
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-upload-dragger) {
  width: 100%;
}

:deep(.el-upload__tip) {
  margin-top: 8px;
  color: #666;
}
</style> 