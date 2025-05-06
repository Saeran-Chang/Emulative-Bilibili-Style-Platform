import { createRouter, createWebHistory } from 'vue-router'
import VideoList from '../views/VideoList.vue'
import Login from '../views/Login.vue'
import VideoPlayer from '../views/VideoPlayer.vue'
import VideoUpload from '../views/VideoUpload.vue'
import VideoEdit from '../views/VideoEdit.vue'
import Favorites from '../views/Favorites.vue'

const routes = [
  {
    path: '/',
    name: 'VideoList',
    component: VideoList,
    meta: {
      keepAlive: true,
      title: '视频列表'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      keepAlive: false,
      title: '登录'
    }
  },
  {
    path: '/video/:id',
    name: 'VideoPlayer',
    component: VideoPlayer,
    meta: {
      keepAlive: true,
      title: '视频播放'
    }
  },
  {
    path: '/upload',
    name: 'VideoUpload',
    component: VideoUpload,
    meta: {
      keepAlive: false,
      title: '上传视频'
    }
  },
  {
    path: '/edit/:id',
    name: 'VideoEdit',
    component: VideoEdit,
    meta: {
      keepAlive: false,
      title: '编辑视频'
    }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites,
    meta: {
      keepAlive: true,
      title: '我的收藏'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || 'Bilibili'
  
  // 检查是否需要登录权限
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

// 全局后置守卫
router.afterEach((to, from) => {
  // 滚动到顶部
  window.scrollTo(0, 0)
})

export default router 