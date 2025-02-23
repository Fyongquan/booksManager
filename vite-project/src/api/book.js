import request from '../utils/request'

// 图书管理相关接口
export const bookApi = {
  // 获取图书列表 - 使用 GET 请求
  getPageList: (params) => request.get('/api/book/list', { params }),

  // 获取图书详情
  getById: (id) => request.get(`/api/book/${id}`),

  // 添加图书
  add: (data) => request.post('/api/book/add', data),

  // 编辑图书
  update: (data) => request.put('/api/book/update', data),

  // 删除图书
  delete: (id) => request.delete(`/api/book/${id}`),

  // 导出Excel
  exportExcel () {
    return request({
      url: '/api/book/excel/export',
      method: 'get',
      responseType: 'blob'  // 关键是设置responseType为blob
    })
  },

  // 导入Excel
  importExcel (file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/api/book/excel/import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
} 