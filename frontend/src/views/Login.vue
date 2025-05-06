<template>
  <div class="login-container">
    <svg class="background-svg" viewBox="0 0 1000 1000" xmlns="http://www.w3.org/2000/svg">
      <path d="M0,0 L1000,0 L1000,1000 L0,1000 Z" fill="#00A1D6"/>
      <path d="M0,0 C200,100 300,300 500,500 C700,700 800,900 1000,1000" stroke-dasharray="10,5"/>
      <path d="M1000,0 C800,100 700,300 500,500 C300,700 200,900 0,1000" stroke-dasharray="10,5"/>
      <path d="M0,500 C200,400 300,300 500,500 C700,700 800,600 1000,500" stroke-dasharray="10,5"/>
      <path d="M500,0 C400,200 300,300 500,500 C700,700 600,800 500,1000" stroke-dasharray="10,5"/>
      <path d="M200,200 C300,250 400,300 500,350" stroke-dasharray="5,3"/>
      <path d="M800,200 C700,250 600,300 500,350" stroke-dasharray="5,3"/>
      <path d="M200,800 C300,750 400,700 500,650" stroke-dasharray="5,3"/>
      <path d="M800,800 C700,750 600,700 500,650" stroke-dasharray="5,3"/>
    </svg>
    <div class="particles">
      <div class="particle"></div>
      <div class="particle"></div>
      <div class="particle"></div>
    </div>
    <div class="wave"></div>
    <div class="wave"></div>
    <div class="login-box">
      <div class="left-section">
        <div class="logo">
          <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
            <!-- 天线 -->
            <path d="M40 20 L60 40 M160 20 L140 40" fill="none" stroke="white" stroke-width="8" stroke-linecap="round"/>
            <!-- 方块 -->
            <rect x="20" y="40" width="160" height="120" fill="none" stroke="white" stroke-width="10" stroke-linecap="round" stroke-linejoin="round"/>
            <!-- 两个点 -->
            <circle cx="60" cy="100" r="8" fill="white"/>
            <circle cx="140" cy="100" r="8" fill="white"/>
          </svg>
          <div class="logo-text">哔哩哔哩</div>
        </div>
        <h1 class="welcome-text">欢迎回来</h1>
        <p>每天都有新发现</p>
      </div>
      <div class="right-section">
        <div class="login-form">
          <h2>账号登录</h2>
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入账号"
                prefix-icon="User"
                class="custom-input"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                class="custom-input"
              />
            </el-form-item>
            <div class="remember-forgot">
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
              <a href="#" class="forgot-password">忘记密码？</a>
            </div>
            <el-button 
              type="primary" 
              class="login-btn" 
              @click="handleLogin"
              :loading="loading"
            >
              登录
            </el-button>
            <div class="register-link">
              还没有账号？<a href="#" @click.prevent="handleRegister">立即注册</a>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'
import emitter from '@/utils/eventBus'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const response = await axios.post('/api/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })
    
    if (response.data.token) {
      localStorage.setItem('token', response.data.token)
      // 获取用户信息
      const userResponse = await axios.get('/api/users/current', {
        headers: {
          'Authorization': `Bearer ${response.data.token}`
        }
      })
      
      // 保存用户信息
      const userInfo = {
        id: userResponse.data.id,
        username: userResponse.data.username,
        nickname: userResponse.data.nickname || userResponse.data.username,
        email: userResponse.data.email
      }
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      
      // 触发登录事件
      emitter.emit('login')
      
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error('登录失败，请检查账号密码')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.response?.data || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

const handleRegister = () => {
  // TODO: 实现注册功能
  ElMessage.info('注册功能开发中...')
}
</script>

<style scoped>
.login-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #00A1D6;
  overflow: hidden;
  z-index: 1;
}

.background-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  opacity: 0.8;
}

.background-svg path {
  fill: none;
  stroke: rgba(255, 255, 255, 0.2);
  stroke-width: 1;
  animation: dash 20s linear infinite;
}

@keyframes dash {
  to {
    stroke-dashoffset: -1000;
  }
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
  pointer-events: none;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: float 15s infinite linear;
}

.particle:nth-child(1) {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.particle:nth-child(2) {
  width: 150px;
  height: 150px;
  top: 60%;
  left: 80%;
  animation-delay: -5s;
}

.particle:nth-child(3) {
  width: 80px;
  height: 80px;
  top: 40%;
  left: 40%;
  animation-delay: -10s;
}

@keyframes float {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(50px, 50px) rotate(90deg);
  }
  50% {
    transform: translate(100px, 0) rotate(180deg);
  }
  75% {
    transform: translate(50px, -50px) rotate(270deg);
  }
  100% {
    transform: translate(0, 0) rotate(360deg);
  }
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: url('data:image/svg+xml;utf8,<svg viewBox="0 0 1200 120" xmlns="http://www.w3.org/2000/svg"><path d="M0,0V46.29c47.79,22.2,103.59,32.17,158,28,70.36-5.37,136.33-33.31,206.8-37.5C438.64,32.43,512.34,53.67,583,72.05c69.27,18,138.3,24.88,209.4,13.08,36.15-6,69.85-17.84,104.45-29.34C989.49,25,1113-14.29,1200,52.47V0Z" fill="rgba(255,255,255,0.1)"/></svg>');
  background-size: 1200px 100px;
  animation: wave 10s linear infinite;
}

.wave:nth-child(2) {
  bottom: 20px;
  opacity: 0.5;
  animation: wave 8s linear infinite;
}

@keyframes wave {
  0% {
    background-position-x: 0;
  }
  100% {
    background-position-x: 1200px;
  }
}

.login-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  display: flex;
  width: 900px;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  z-index: 10;
  transition: transform 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 
    0 12px 48px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.3);
}

.left-section {
  flex: 1;
  padding: 60px;
  background: linear-gradient(135deg, rgba(0,161,214,0.9) 0%, rgba(35,173,229,0.9) 100%);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.left-section .logo {
  width: 160px;
  height: 120px;
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.left-section .logo svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

.left-section .logo-text {
  position: absolute;
  bottom: -30px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
  font-size: 24px;
  font-weight: bold;
  white-space: nowrap;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.left-section .welcome-text {
  font-size: 32px;
  margin: 40px 0 10px 0;
  position: relative;
  z-index: 1;
}

.left-section p {
  font-size: 18px;
  opacity: 0.9;
  margin: 0;
  position: relative;
  z-index: 1;
}

.right-section {
  flex: 1;
  padding: 60px;
  background: white;
  position: relative;
}

.login-form {
  max-width: 320px;
  margin: 0 auto;
}

.login-form h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 40px;
  text-align: center;
  font-weight: 600;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.forgot-password {
  color: #00a1d6;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #23ade5;
}

.login-btn {
  width: 100%;
  height: 44px;
  margin-bottom: 20px;
  background: #00a1d6;
  border: none;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: #23ade5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 161, 214, 0.3);
}

.register-link {
  text-align: center;
  color: #666;
  font-size: 14px;
}

.register-link a {
  color: #00a1d6;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #23ade5;
}

:deep(.custom-input .el-input__wrapper) {
  height: 44px;
  padding: 0 15px;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s ease;
}

:deep(.custom-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #00a1d6 inset;
}

:deep(.custom-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #00a1d6 inset;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #00a1d6;
  border-color: #00a1d6;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #00a1d6;
}
</style> 