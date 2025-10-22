import request from '@/utils/request'

/**
 * 前台公开接口 API
 */

// 获取首页数据
export const getHomePageData = () => {
  return request({
    url: '/public/home',
    method: 'get'
  })
}

// 获取公告列表（分页+搜索）
export const getNoticeList = (params) => {
  return request({
    url: '/public/notices',
    method: 'get',
    params
  })
}

// 获取公告详情
export const getNoticeDetail = (id) => {
  return request({
    url: `/public/notices/${id}`,
    method: 'get'
  })
}

// 获取本周食谱
export const getWeeklyMenu = () => {
  return request({
    url: '/public/menu/weekly',
    method: 'get'
  })
}

// 获取师资列表
export const getTeacherList = () => {
  return request({
    url: '/public/teachers',
    method: 'get'
  })
}

// 获取相册列表
export const getAlbumList = (params) => {
  return request({
    url: '/public/albums',
    method: 'get',
    params
  })
}

// 获取相册详情
export const getAlbumDetail = (id) => {
  return request({
    url: `/public/albums/${id}`,
    method: 'get'
  })
}

// 在线报名
export const submitEnrollment = (data) => {
  return request({
    url: '/public/enrollment',
    method: 'post',
    data
  })
}

// 上传报名附件
export const uploadEnrollmentFile = (formData) => {
  return request({
    url: '/public/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
