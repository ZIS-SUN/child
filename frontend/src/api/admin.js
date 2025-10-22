import request from '@/utils/request'

// 获取数据面板
export function getDashboard() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

// 用户管理
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function addUser(data) {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function resetPassword(id) {
  return request({
    url: `/admin/users/${id}/reset-password`,
    method: 'put'
  })
}

// 幼儿管理
export function getChildList(params) {
  return request({
    url: '/admin/children',
    method: 'get',
    params
  })
}

export function addChild(data) {
  return request({
    url: '/admin/children',
    method: 'post',
    data
  })
}

export function updateChild(id, data) {
  return request({
    url: `/admin/children/${id}`,
    method: 'put',
    data
  })
}

export function deleteChild(id) {
  return request({
    url: `/admin/children/${id}`,
    method: 'delete'
  })
}

// 班级管理
export function getClassList(params) {
  return request({
    url: '/admin/classes',
    method: 'get',
    params
  })
}

export function addClass(data) {
  return request({
    url: '/admin/classes',
    method: 'post',
    data
  })
}

export function updateClass(id, data) {
  return request({
    url: `/admin/classes/${id}`,
    method: 'put',
    data
  })
}

export function deleteClass(id) {
  return request({
    url: `/admin/classes/${id}`,
    method: 'delete'
  })
}

// 请假审批
export function getLeaveList(params) {
  return request({
    url: '/admin/leave',
    method: 'get',
    params
  })
}

export function approveLeave(id, remark) {
  return request({
    url: `/admin/leave/${id}/approve`,
    method: 'put',
    params: { remark }
  })
}

export function rejectLeave(id, remark) {
  return request({
    url: `/admin/leave/${id}/reject`,
    method: 'put',
    params: { remark }
  })
}

// 通知管理
export function getNoticeList(params) {
  return request({
    url: '/admin/notices',
    method: 'get',
    params
  })
}

export function addNotice(data) {
  return request({
    url: '/admin/notices',
    method: 'post',
    data
  })
}

export function updateNotice(id, data) {
  return request({
    url: `/admin/notices/${id}`,
    method: 'put',
    data
  })
}

export function deleteNotice(id) {
  return request({
    url: `/admin/notices/${id}`,
    method: 'delete'
  })
}

// 文件上传
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
