<template>
  <div class="video-edit">
    <el-card class="edit-card">
      <template #header>
        <div class="card-header">
          <h2>编辑视频</h2>
        </div>
      </template>
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="视频描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入视频描述"
          />
        </el-form-item>
        <el-form-item label="封面图片" prop="cover">
          <el-upload
            class="upload-dragger"
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
          </el-upload>
          <div class="current-cover" v-if="currentCover">
            <img :src="`/api/videos/stream/${currentCover}`" alt="当前封面">
            <span>当前封面</span>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">
            保存修改
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const editFormRef = ref(null)
const loading = ref(false)
const currentCover = ref('')

const editForm = reactive({
  title: '',
  description: '',
  cover: null
})

const rules = {
  title: [
    { required: true, message: '请输入视频标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入视频描述', trigger: 'blur' }
  ]
}

const fetchVideo = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    const response = await axios.get(`/api/videos/${route.params.id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    const video = response.data
    editForm.title = video.title
    editForm.description = video.description
    currentCover.value = video.coverPath
  } catch (error) {
    ElMessage.error('获取视频信息失败')
  }
}

const handleCoverChange = (file) => {
  editForm.cover = file.raw
}

const handleSave = async () => {
  if (!editFormRef.value) return
  
  try {
    await editFormRef.value.validate()
    loading.value = true
    
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }

    const formData = new FormData()
    formData.append('title', editForm.title)
    formData.append('description', editForm.description)
    if (editForm.cover) {
      formData.append('cover', editForm.cover)
    }
    
    await axios.put(`/api/videos/${route.params.id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      }
    })
    
    ElMessage.success('修改成功')
    router.push('/')
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchVideo()
})
</script>

<style scoped>
.video-edit {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.edit-card {
  margin-top: 20px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: var(--primary-color);
}

.upload-dragger {
  width: 100%;
}

.current-cover {
  margin-top: 20px;
  text-align: center;
}

.current-cover img {
  max-width: 200px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.current-cover span {
  display: block;
  color: #666;
  font-size: 14px;
}
</style> 