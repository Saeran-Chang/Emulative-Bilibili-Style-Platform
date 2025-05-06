import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'

const app = createApp(App)

// 配置axios默认值
axios.defaults.baseURL = 'http://localhost:8080'

app.use(ElementPlus)
app.use(router)
app.mount('#app') 