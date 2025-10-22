import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => state.userInfo.role || '',
    isParent: (state) => state.userInfo.role === 'PARENT',
    isAdmin: (state) => state.userInfo.role === 'PRINCIPAL' || state.userInfo.role === 'TEACHER'
  },

  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    login(loginResponse) {
      this.setToken(loginResponse.token)
      this.setUserInfo({
        userId: loginResponse.userId,
        username: loginResponse.username,
        realName: loginResponse.realName,
        role: loginResponse.role,
        avatarUrl: loginResponse.avatarUrl
      })
    },

    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
