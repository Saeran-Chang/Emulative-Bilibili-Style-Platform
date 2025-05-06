<template>
  <div class="user">
    <el-card v-if="user" class="user-card">
      <template #header>
        <div class="card-header">
          <span>用户信息</span>
        </div>
      </template>
      <div class="user-info">
        <p>用户名：{{ user.username }}</p>
        <p>昵称：{{ user.nickname }}</p>
        <p>邮箱：{{ user.email }}</p>
      </div>
    </el-card>
    <el-button @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

interface User {
  id: number
  username: string
  nickname: string
  email: string
}

const route = useRoute()
const user = ref<User | null>(null)

onMounted(async () => {
  try {
    const response = await axios.get(`/api/users/${route.params.id}`)
    user.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})
</script>

<style scoped>
.user {
  padding: 20px;
}

.user-card {
  max-width: 500px;
  margin: 0 auto 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  text-align: left;
}
</style> 