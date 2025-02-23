import request from '../utils/request'

// 用户管理相关接口
export const userApi = {
  // 获取用户列表
  list4Table: (params) => request.get('/api/user/query', { params }),

  // 获取所有用户
  getAllUsers: () => request.get('/api/user/list'),

  // 批量保存用户
  batchSave: (data) => request.post('/api/user/batchSave', data),

  // 编辑用户
  edit: (data) => request.post('/api/user/update', data),

  // 删除用户
  remove: (userId) => request.delete(`/api/user/${userId}`),

  // 重置密码
  resetPassword: (userId) => request.post(`/api/user/resetPassword/${userId}`),

  // 批量删除用户
  batchRemove: (userIds) => request.post('/api/user/batchRemove', { userIds }),

  // 修改密码
  updatePassword: (data) => request.post('/api/user/updatePassword', data),

  // 获取当前用户信息
  getUserInfo: () => request.get('/api/user/info'),

  // 获取用户总数
  getUserCount: () => request.get('/api/user/count'),

  // 添加用户
  add: (data) => request.post('/api/user/add', data),

  // 用户注册
  register: (data) => request.post('/api/register/add', data),

  // 用户修改信息
  profile: (data) => request.put('/api/user/profile', data),

  // 上传头像
  uploadAvatar (userId, file) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId)
    return request({
      url: '/api/user/avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取用户列表（新版）
  getList (params) {
    return request.get('/api/user/query', { params })
  },

  // 获取用户总数（新版）
  getCount () {
    return request.get('/api/user/count')
  },

  // 更新用户（新版）
  update (data) {
    return request.post('/api/user/update', data)
  }
} 