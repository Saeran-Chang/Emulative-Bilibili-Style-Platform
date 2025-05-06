const handleUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要上传的视频文件')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)
  formData.append('title', title.value)
  formData.append('description', description.value)

  try {
    const response = await axios.post('/api/videos/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    ElMessage.success('视频上传成功')
    emit('upload-success')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '视频上传失败')
  }
} 