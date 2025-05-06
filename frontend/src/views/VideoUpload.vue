<template>
  <div class="video-upload">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <h2>上传视频</h2>
        </div>
      </template>
      <el-form :model="uploadForm" :rules="rules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="视频描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入视频描述"
          />
        </el-form-item>
        <el-form-item label="视频文件" prop="file">
          <el-upload
            class="upload-dragger"
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
          </el-upload>
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
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpload" :loading="loading">
            上传视频
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const uploadFormRef = ref(null)
const loading = ref(false)

const uploadForm = reactive({
  title: '',
  description: '',
  file: null,
  cover: null
})

const rules = {
  title: [
    { required: true, message: '请输入视频标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入视频描述', trigger: 'blur' }
  ],
  file: [
    { required: true, message: '请选择视频文件', trigger: 'change' }
  ],
  cover: [
    { required: true, message: '请选择封面图片', trigger: 'change' }
  ]
}

const handleVideoChange = (file) => {
  uploadForm.file = file.raw
}

const handleCoverChange = (file) => {
  uploadForm.cover = file.raw
}

const handleUpload = async () => {
  if (!uploadFormRef.value) return
  
  try {
    await uploadFormRef.value.validate()
    loading.value = true
    
    const formData = new FormData()
    formData.append('file', uploadForm.file)
    formData.append('title', uploadForm.title)
    formData.append('description', uploadForm.description)
    formData.append('cover', uploadForm.cover)
    
    await axios.post('/api/videos/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    ElMessage.success('视频上传成功')
    router.push('/')
  } catch (error) {
    ElMessage.error('视频上传失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.video-upload {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.upload-card {
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
</style> 