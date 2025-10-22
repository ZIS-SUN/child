<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bubble bubble-1"></div>
      <div class="bubble bubble-2"></div>
      <div class="bubble bubble-3"></div>
      <div class="bubble bubble-4"></div>
      <div class="bubble bubble-5"></div>
    </div>

    <el-card class="login-card">
      <div class="login-header">
        <div class="logo-container">
          <div class="logo-icon">🎓</div>
        </div>
        <h2 class="title">幼儿园信息管理系统</h2>
        <p class="subtitle">用心守护每一个孩子的成长</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane name="parent">
          <template #label>
            <span class="tab-label">
              <el-icon><User /></el-icon>
              <span>家长登录</span>
            </span>
          </template>
          <el-form ref="parentFormRef" :model="parentForm" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="parentForm.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="parentForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="handleParentLogin"
                :loading="loading"
                size="large"
                class="login-button"
              >
                <span v-if="!loading">登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
            <el-form-item class="register-link">
              <router-link to="/register">
                还没有账号？<span class="link-text">立即注册</span>
              </router-link>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane name="admin">
          <template #label>
            <span class="tab-label">
              <el-icon><Management /></el-icon>
              <span>管理员</span>
            </span>
          </template>
          <el-form ref="adminFormRef" :model="adminForm" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="adminForm.username"
                placeholder="请输入管理员账号"
                size="large"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="adminForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="handleAdminLogin"
                :loading="loading"
                size="large"
                class="login-button"
              >
                <span v-if="!loading">登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Management } from '@element-plus/icons-vue'
import { parentLogin, adminLogin } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('parent')
const loading = ref(false)

const parentForm = ref({
  username: '',
  password: ''
})

const adminForm = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const parentFormRef = ref(null)
const adminFormRef = ref(null)

const handleParentLogin = async () => {
  await parentFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await parentLogin(parentForm.value)
        userStore.login(res.data)
        ElMessage.success('登录成功')
        router.push('/parent/home')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleAdminLogin = async () => {
  await adminFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await adminLogin(adminForm.value)
        userStore.login(res.data)
        ElMessage.success('登录成功')
        router.push('/admin/dashboard')
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-background {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.bubble {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 15s infinite ease-in-out;
}

.bubble-1 {
  width: 80px;
  height: 80px;
  left: 10%;
  top: 20%;
  animation-delay: 0s;
}

.bubble-2 {
  width: 60px;
  height: 60px;
  left: 80%;
  top: 40%;
  animation-delay: 2s;
}

.bubble-3 {
  width: 100px;
  height: 100px;
  left: 60%;
  top: 70%;
  animation-delay: 4s;
}

.bubble-4 {
  width: 50px;
  height: 50px;
  left: 30%;
  top: 80%;
  animation-delay: 1s;
}

.bubble-5 {
  width: 70px;
  height: 70px;
  left: 85%;
  top: 15%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) translateX(0) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-30px) translateX(20px) scale(1.1);
    opacity: 0.8;
  }
}

.login-card {
  position: relative;
  width: 480px;
  padding: 40px 50px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.6s ease-out;
  z-index: 10;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 35px;
}

.logo-container {
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 60px;
  display: inline-block;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 15px 0 10px;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0;
  font-weight: 400;
}

.login-tabs {
  margin-top: 25px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.login-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 25px;
}

.login-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 8px 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.login-button:active {
  transform: translateY(0);
}

.register-link {
  text-align: center;
  margin-bottom: 0;
}

.register-link a {
  color: #7f8c8d;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.register-link .link-text {
  color: #667eea;
  font-weight: 600;
  margin-left: 4px;
}

.register-link a:hover .link-text {
  color: #764ba2;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
    padding: 30px 25px;
  }

  .title {
    font-size: 24px;
  }
}
</style>
