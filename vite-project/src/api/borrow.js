import request from '../utils/request'

export const borrowApi = {
  // 获取借阅列表
  getList: (params) => request.get('/api/borrow/list', { params }),

  // 获取个人的借阅列表
  getMyBorrows: (params) => request.get('/api/borrow/myBorrows', { params }),

  // 借阅图书
  borrow: (data) => request.post('/api/borrow', data),

  // 归还图书
  return: (borrowId) => request.post(`/api/borrow/${borrowId}/return`),

  // 续借图书
  renew: (borrowId) => request.post(`/api/borrow/${borrowId}/renew`)
} 