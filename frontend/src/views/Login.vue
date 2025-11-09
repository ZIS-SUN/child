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
/* ========== 鸿蒙风格登录页面 ========== */

.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 50%, #005BB5 100%);
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
  background: rgba(255, 255, 255, 0.08);
  animation: float 20s infinite ease-in-out;
  backdrop-filter: blur(2px);
}

.bubble-1 {
  width: 120px;
  height: 120px;
  left: 8%;
  top: 15%;
  animation-delay: 0s;
}

.bubble-2 {
  width: 80px;
  height: 80px;
  left: 75%;
  top: 35%;
  animation-delay: 3s;
}

.bubble-3 {
  width: 150px;
  height: 150px;
  left: 55%;
  top: 65%;
  animation-delay: 6s;
}

.bubble-4 {
  width: 60px;
  height: 60px;
  left: 25%;
  top: 75%;
  animation-delay: 2s;
}

.bubble-5 {
  width: 100px;
  height: 100px;
  left: 88%;
  top: 10%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.5;
  }
  33% {
    transform: translate(30px, -40px) scale(1.1);
    opacity: 0.7;
  }
  66% {
    transform: translate(-20px, -20px) scale(0.9);
    opacity: 0.6;
  }
}

.login-card {
  position: relative;
  width: 500px;
  padding: 48px 56px;
  backdrop-filter: blur(40px);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 32px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 
    0 24px 64px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  animation: slideIn 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 10;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-container {
  margin-bottom: 24px;
}

.logo-icon {
  font-size: 72px;
  display: inline-block;
  animation: bounce 3s infinite ease-in-out;
  filter: drop-shadow(0 4px 12px rgba(0, 125, 255, 0.3));
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-12px) rotate(-3deg);
  }
  75% {
    transform: translateY(-8px) rotate(3deg);
  }
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #1D1D1F;
  margin: 16px 0 12px;
  letter-spacing: 0.5px;
}

.subtitle {
  font-size: 15px;
  color: #86868B;
  margin: 0;
  font-weight: 400;
  letter-spacing: 0.3px;
}

.login-tabs {
  margin-top: 32px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.login-tabs :deep(.el-tabs__item) {
  font-size: 17px;
  font-weight: 500;
  padding: 0 28px;
  color: #86868B;
  transition: all 0.3s ease;
}

.login-tabs :deep(.el-tabs__item.is-active) {
  color: #007DFF;
  font-weight: 600;
}

.login-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(90deg, #007DFF 0%, #0066CC 100%);
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 10px;
}

.login-form {
  margin-top: 36px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 28px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 16px;
  padding: 12px 18px;
  background: #F5F5F9;
  box-shadow: none;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  background: #EBEBF0;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  background: #FFFFFF;
  border-color: #007DFF;
  box-shadow: 0 0 0 4px rgba(0, 125, 255, 0.1);
}

.login-form :deep(.el-input__inner) {
  font-size: 15px;
  color: #1D1D1F;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #AEAEB2;
}

.login-button {
  width: 100%;
  height: 52px;
  font-size: 17px;
  font-weight: 600;
  border-radius: 16px;
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 100%);
  border: none;
  box-shadow: 
    0 8px 20px rgba(0, 125, 255, 0.3),
    0 0 0 1px rgba(0, 125, 255, 0.1) inset;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  letter-spacing: 0.5px;
}

.login-button:hover {
  transform: translateY(-3px);
  box-shadow: 
    0 12px 28px rgba(0, 125, 255, 0.4),
    0 0 0 1px rgba(0, 125, 255, 0.2) inset;
}

.login-button:active {
  transform: translateY(-1px);
  box-shadow: 
    0 6px 16px rgba(0, 125, 255, 0.3),
    0 0 0 1px rgba(0, 125, 255, 0.1) inset;
}

.register-link {
  text-align: center;
  margin-bottom: 0;
  margin-top: 8px;
}

.register-link a {
  color: #86868B;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
}

.register-link .link-text {
  color: #007DFF;
  font-weight: 600;
  margin-left: 6px;
}

.register-link a:hover .link-text {
  color: #0066CC;
  text-decoration: none;
}

@media (max-width: 768px) {
  .login-card {
    width: 92%;
    padding: 36px 28px;
    border-radius: 28px;
  }

  .logo-icon {
    font-size: 60px;
  }

  .title {
    font-size: 26px;
  }

  .subtitle {
    font-size: 14px;
  }

  .login-button {
    height: 48px;
    font-size: 16px;
  }
}
</style>
