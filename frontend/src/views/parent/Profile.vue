<template>
  <div class="profile-container">
    <h2>个人中心</h2>

    <!-- 个人信息 -->
    <el-card style="margin-bottom: 20px">
      <template #header>
        <span>个人信息</span>
      </template>
      <el-form :model="profileForm" label-width="100px">
        <el-form-item label="头像">
          <div class="avatar-uploader">
            <el-avatar :size="80" :src="profileForm.avatarUrl || '/default-avatar.png'" />
            <el-upload
              class="upload-demo"
              action="/api/upload/image"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-button size="small" style="margin-left: 20px">上传头像</el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="profileForm.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码 -->
    <el-card>
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updatePassword">修改密码</el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProfile, updateProfile as updateProfileApi, updatePassword as updatePasswordApi } from '@/api/parent'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const profileForm = ref({
  username: '',
  realName: '',
  phone: '',
  avatarUrl: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

const fetchProfile = async () => {
  try {
    const res = await getProfile()
    profileForm.value = res.data
  } catch (error) {
    ElMessage.error('获取个人信息失败')
  }
}

const updateProfile = async () => {
  if (!profileForm.value.realName || !profileForm.value.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    await updateProfileApi(profileForm.value)
    ElMessage.success('个人信息更新成功')
    // 更新本地存储的用户信息
    userStore.setUserInfo({
      ...userStore.userInfo,
      realName: profileForm.value.realName,
      phone: profileForm.value.phone,
      avatarUrl: profileForm.value.avatarUrl
    })
    fetchProfile()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const updatePassword = async () => {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位')
    return
  }

  try {
    await updatePasswordApi({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    resetPasswordForm()
    // 清除登录信息，跳转到登录页
    setTimeout(() => {
      userStore.logout()
      window.location.href = '/login'
    }, 1500)
  } catch (error) {
    ElMessage.error('密码修改失败，请检查原密码是否正确')
  }
}

const resetPasswordForm = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    profileForm.value.avatarUrl = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.avatar-uploader {
  display: flex;
  align-items: center;
}
</style>
