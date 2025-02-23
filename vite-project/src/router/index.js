import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'
import { navigateToLogin } from '@/utils/navigation'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Layout',
    component: () => import('../layout/index.vue'),
    redirect: '/home/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'books',
        name: 'Books',
        component: () => import('../views/books/index.vue'),
        meta: { title: '图书管理', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/users/index.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: () => import('../views/borrow/index.vue'),
        meta: { title: '借阅管理', requiresAuth: true, roles: ['ADMIN'] }
      },
      {
        path: 'borrowBooks',
        name: 'BorrowBooks',
        component: () => import('../views/borrowBooks/index.vue'),
        meta: { title: '借阅图书', requiresAuth: true }
      }
    ]
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: {
      title: '用户注册',
      requiresAuth: false
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加全局前置守卫
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const whiteList = ['/login', '/register']

  if (whiteList.includes(to.path)) {
    if (token && to.path === '/login') {
      // next('/home/dashboard')
      next()
    } else {
      next()
    }
  } else {
    if (!token) {
      ElMessage.warning('请先登录')
      next(`/login?redirect=${to.path}`)
    } else {
      try {
        const response = await service.get('/api/login/token', {
          headers: { token }
        })

        if (response.msgResult === 'success') {
          next()
        } else {
          ElMessage.warning('登录已过期，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('loginInfo')
          next('/login')
        }
      } catch (error) {
        ElMessage.warning('登录已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('loginInfo')
        next('/login')
      }
    }
  }
})

export default router 