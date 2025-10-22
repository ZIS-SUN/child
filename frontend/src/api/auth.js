import request from '@/utils/request'

// 家长注册
export function parentRegister(data) {
  return request({
    url: '/parent/register',
    method: 'post',
    data
  })
}

// 家长登录
export function parentLogin(data) {
  return request({
    url: '/parent/login',
    method: 'post',
    data
  })
}

// 管理员登录
export function adminLogin(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/parent/logout',
    method: 'post'
  })
}
