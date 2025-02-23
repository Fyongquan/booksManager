import axios from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'

// 添加一个标志位，用于控制是否已经显示了错误提示
let isShowingError = false

const service = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 如果是文件上传，不要覆盖已设置的 Content-Type
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }

    const token = localStorage.getItem('token')
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果是文件流，直接返回
    if (response.config.responseType === 'blob') {
      return response.data
    }

    const res = response.data
    if (res.msgResult === 'error') {
      if (res.msgInfo === '无效的token' || res.msgInfo === 'token已过期') {
        // token无效或过期，清除本地存储并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('loginInfo')
        // 跳转到登录页
        router.push('/login')
        ElMessage.warning('登录已过期，请重新登录')
        return Promise.reject(new Error('token已过期'))
      }
      ElMessage.error(res.msgInfo || '系统错误')
      return Promise.reject(new Error(res.msgInfo || '系统错误'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 如果已经显示了错误提示，则不再显示
          if (!isShowingError) {
            isShowingError = true
            // token失效，清除用户信息
            localStorage.removeItem('token')
            localStorage.removeItem('loginInfo')
            ElMessage.error(error.response.data.info)
            // 跳转到登录页
            router.push('/login')
            // 设置一个延时，重置标志位
            setTimeout(() => {
              isShowingError = false
            }, 2000) // 2秒后重置
          }
          break
        default:
          if (!isShowingError) {
            isShowingError = true
            ElMessage.error('系统错误')
            setTimeout(() => {
              isShowingError = false
            }, 2000)
          }
      }
    }
    return Promise.reject(error)
  }
)

export default service 