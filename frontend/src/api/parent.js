import request from '@/utils/request'

// 获取首页数据
export function getHomeData() {
  return request({
    url: '/parent/home',
    method: 'get'
  })
}

// 获取我的孩子列表
export function getMyChildren() {
  return request({
    url: '/parent/children',
    method: 'get'
  })
}

// 获取孩子详情
export function getChildDetail(id) {
  return request({
    url: `/parent/children/${id}`,
    method: 'get'
  })
}

// 获取通知列表
export function getNoticeList(params) {
  return request({
    url: '/parent/notices',
    method: 'get',
    params
  })
}

// 获取通知详情
export function getNoticeDetail(id) {
  return request({
    url: `/parent/notices/${id}`,
    method: 'get'
  })
}

// 标记通知已读
export function markNoticeRead(id) {
  return request({
    url: `/parent/notices/${id}/read`,
    method: 'post'
  })
}

// 别名导出，用于兼容
export const markNoticeAsRead = markNoticeRead

// 提交请假申请
export function submitLeaveRequest(data) {
  return request({
    url: '/parent/leave',
    method: 'post',
    data
  })
}

// 获取请假记录
export function getLeaveList() {
  return request({
    url: '/parent/leave',
    method: 'get'
  })
}

// 撤销请假
export function cancelLeaveRequest(id) {
  return request({
    url: `/parent/leave/${id}/cancel`,
    method: 'put'
  })
}

// 获取个人信息
export function getProfile() {
  return request({
    url: '/parent/profile',
    method: 'get'
  })
}

// 更新个人信息
export function updateProfile(data) {
  return request({
    url: '/parent/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function updatePassword(data) {
  return request({
    url: '/parent/password',
    method: 'put',
    data
  })
}

// 考勤查询
export function getAttendanceCalendar(params) {
  return request({
    url: '/parent/attendance/calendar',
    method: 'get',
    params
  })
}

export function getAttendanceStats(params) {
  return request({
    url: '/parent/attendance/stats',
    method: 'get',
    params
  })
}

// 相册查看
export function getMyAlbums() {
  return request({
    url: '/parent/albums',
    method: 'get'
  })
}

export function getAlbumDetail(id) {
  return request({
    url: `/parent/albums/${id}`,
    method: 'get'
  })
}

export function getAlbumPhotos(id) {
  return request({
    url: `/parent/albums/${id}/photos`,
    method: 'get'
  })
}

// 健康管理
export function getChildHealthInfo(childId) {
  return request({
    url: `/parent/health/${childId}`,
    method: 'get'
  })
}

export function getChildTemperatureRecords(childId, params) {
  return request({
    url: `/parent/health/${childId}/temperature`,
    method: 'get',
    params
  })
}

export function getChildGrowthRecords(childId) {
  return request({
    url: `/parent/health/${childId}/growth`,
    method: 'get'
  })
}

export function getRecentTemperature(childId) {
  return request({
    url: `/parent/health/${childId}/temperature/recent`,
    method: 'get'
  })
}
