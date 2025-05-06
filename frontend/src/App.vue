<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-content">
        <div class="logo">
          <img src="@/assets/logo.svg" alt="Bilibili" class="logo-img">
          <h1>Bilibili</h1>
        </div>
        <div class="user-info" v-if="userInfo">
          <el-dropdown>
            <span class="user-dropdown">
              <el-avatar :size="32" :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${userInfo.username}`" />
              <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/favorites')">
                  <el-icon><Collection /></el-icon>
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <el-button v-else type="primary" @click="$router.push('/login')" class="login-btn">
          <el-icon><User /></el-icon>
          登录
        </el-button>
      </div>
    </el-header>
    <el-main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, SwitchButton, Collection, ArrowDown } from '@element-plus/icons-vue'
import emitter from '@/utils/eventBus'

const router = useRouter()
const userInfo = ref(null)

// 监听本地存储变化
const updateUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    userInfo.value = JSON.parse(userInfoStr)
  } else {
    userInfo.value = null
  }
}

// 处理登录事件
const handleLogin = () => {
  updateUserInfo()
}

onMounted(() => {
  updateUserInfo()
  
  // 监听storage事件，以便在其他标签页中更新状态
  window.addEventListener('storage', (e) => {
    if (e.key === 'userInfo') {
      updateUserInfo()
    }
  })

  // 监听登录事件
  emitter.on('login', handleLogin)
})

onUnmounted(() => {
  // 清理事件监听
  emitter.off('login', handleLogin)
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  userInfo.value = null
  router.push('/login')
}
</script>

<style>
:root {
  --primary-color: #00a1d6;
  --primary-hover: #00b5e6;
  --bg-color: #f4f5f7;
  --header-bg: #ffffff;
  --text-color: #333333;
  --border-color: #e4e7ed;
}

body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.app-container {
  min-height: 100vh;
}

.app-header {
  background-color: var(--header-bg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  width: 100%;
  z-index: 1000;
  height: 60px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-img {
  height: 32px;
  width: auto;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  color: var(--primary-color);
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.username {
  font-weight: 500;
  color: var(--text-color);
}

.login-btn {
  display: flex;
  align-items: center;
  gap: 5px;
}

.app-main {
  padding-top: 60px;
  min-height: calc(100vh - 60px);
  background-image: url('@/assets/VideoBackground.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 5px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 5px;
}
</style> 