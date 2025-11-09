<template>
  <div class="register-container">
    <div class="register-background">
      <div class="bubble bubble-1"></div>
      <div class="bubble bubble-2"></div>
      <div class="bubble bubble-3"></div>
    </div>

    <el-card class="register-card">
      <div class="register-header">
        <div class="logo-icon">👨‍👩‍👧‍👦</div>
        <h2 class="title">家长注册</h2>
        <p class="subtitle">加入我们，共同见证孩子的成长</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="register-form">
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名"
            size="large"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码（至少6位）" 
            size="large"
            show-password 
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input 
            v-model="form.realName" 
            placeholder="请输入真实姓名" 
            size="large"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入手机号" 
            size="large"
            clearable
          />
        </el-form-item>
        <el-form-item label="与孩子关系" prop="relationship">
          <el-select 
            v-model="form.relationship" 
            placeholder="请选择与孩子的关系" 
            size="large"
            style="width: 100%"
          >
            <el-option label="父亲" value="父亲" />
            <el-option label="母亲" value="母亲" />
            <el-option label="祖父" value="祖父" />
            <el-option label="祖母" value="祖母" />
            <el-option label="外祖父" value="外祖父" />
            <el-option label="外祖母" value="外祖母" />
          </el-select>
        </el-form-item>
        <el-form-item label="家庭住址" prop="address">
          <el-input 
            v-model="form.address" 
            type="textarea" 
            placeholder="请输入家庭住址"
            :rows="3"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            :loading="loading" 
            size="large"
            class="register-button"
          >
            <span v-if="!loading">立即注册</span>
            <span v-else>注册中...</span>
          </el-button>
        </el-form-item>
        <el-form-item class="login-link">
          <router-link to="/login">
            已有账号？<span class="link-text">立即登录</span>
          </router-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { parentRegister } from '@/api/auth'

const router = useRouter()
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  relationship: '',
  address: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  relationship: [{ required: true, message: '请选择与孩子的关系', trigger: 'change' }]
}

const formRef = ref(null)

const handleRegister = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await parentRegister(form.value)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* ========== 鸿蒙风格注册页面 ========== */

.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #00CC88 0%, #00AA73 50%, #008F5D 100%);
  padding: 20px;
}

.register-background {
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
  width: 140px;
  height: 140px;
  left: 10%;
  top: 20%;
  animation-delay: 0s;
}

.bubble-2 {
  width: 100px;
  height: 100px;
  left: 80%;
  top: 50%;
  animation-delay: 4s;
}

.bubble-3 {
  width: 120px;
  height: 120px;
  left: 50%;
  top: 70%;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(30px, -30px) scale(1.1);
    opacity: 0.7;
  }
}

.register-card {
  position: relative;
  width: 600px;
  max-width: 95%;
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

.register-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-icon {
  font-size: 64px;
  display: inline-block;
  margin-bottom: 16px;
  animation: bounce 3s infinite ease-in-out;
  filter: drop-shadow(0 4px 12px rgba(0, 204, 136, 0.3));
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
  color: #1D1D1F;
  margin: 0 0 12px 0;
  letter-spacing: 0.5px;
}

.subtitle {
  font-size: 15px;
  color: #86868B;
  margin: 0;
  font-weight: 400;
  letter-spacing: 0.3px;
}

.register-form {
  margin-top: 24px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.register-form :deep(.el-form-item__label) {
  font-size: 15px;
  font-weight: 500;
  color: #1D1D1F;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 16px;
  padding: 12px 18px;
  background: #F5F5F9;
  box-shadow: none;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.register-form :deep(.el-input__wrapper:hover),
.register-form :deep(.el-select .el-input__wrapper:hover) {
  background: #EBEBF0;
}

.register-form :deep(.el-input__wrapper.is-focus),
.register-form :deep(.el-select.is-focused .el-input__wrapper) {
  background: #FFFFFF;
  border-color: #00CC88;
  box-shadow: 0 0 0 4px rgba(0, 204, 136, 0.1);
}

.register-form :deep(.el-textarea__inner) {
  border-radius: 16px;
  padding: 12px 18px;
  background: #F5F5F9;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  font-family: inherit;
  resize: none;
}

.register-form :deep(.el-textarea__inner:hover) {
  background: #EBEBF0;
}

.register-form :deep(.el-textarea__inner:focus) {
  background: #FFFFFF;
  border-color: #00CC88;
  box-shadow: 0 0 0 4px rgba(0, 204, 136, 0.1);
  outline: none;
}

.register-form :deep(.el-input__inner) {
  font-size: 15px;
  color: #1D1D1F;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #AEAEB2;
}

.register-button {
  width: 100%;
  height: 52px;
  font-size: 17px;
  font-weight: 600;
  border-radius: 16px;
  background: linear-gradient(135deg, #00CC88 0%, #00AA73 100%);
  border: none;
  box-shadow: 
    0 8px 20px rgba(0, 204, 136, 0.3),
    0 0 0 1px rgba(0, 204, 136, 0.1) inset;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  letter-spacing: 0.5px;
}

.register-button:hover {
  transform: translateY(-3px);
  box-shadow: 
    0 12px 28px rgba(0, 204, 136, 0.4),
    0 0 0 1px rgba(0, 204, 136, 0.2) inset;
}

.register-button:active {
  transform: translateY(-1px);
  box-shadow: 
    0 6px 16px rgba(0, 204, 136, 0.3),
    0 0 0 1px rgba(0, 204, 136, 0.1) inset;
}

.login-link {
  text-align: center;
  margin-bottom: 0;
}

.login-link a {
  color: #86868B;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
}

.login-link .link-text {
  color: #00CC88;
  font-weight: 600;
  margin-left: 6px;
}

.login-link a:hover .link-text {
  color: #00AA73;
  text-decoration: none;
}

@media (max-width: 768px) {
  .register-card {
    width: 95%;
    padding: 36px 28px;
    border-radius: 28px;
  }

  .logo-icon {
    font-size: 56px;
  }

  .title {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
  }

  .register-form :deep(.el-form-item__label) {
    font-size: 14px;
  }

  .register-button {
    height: 48px;
    font-size: 16px;
  }
}
</style>
