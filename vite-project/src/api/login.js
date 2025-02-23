import request from '../utils/request'

export const loginApi = {
  // 获取验证码
  getCode: () => request.get('/api/login/code'),

  // 登录验证
  check: (data) => request.post('/api/login/check', data)
} 