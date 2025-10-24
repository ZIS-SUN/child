<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <h3>家长端</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/parent/home">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/parent/children">
          <el-icon><User /></el-icon>
          <span>孩子信息</span>
        </el-menu-item>
        <el-menu-item index="/parent/notices">
          <el-icon><Bell /></el-icon>
          <span>通知公告</span>
        </el-menu-item>
        <el-menu-item index="/parent/attendance">
          <el-icon><Calendar /></el-icon>
          <span>考勤记录</span>
        </el-menu-item>
        <el-menu-item index="/parent/leave">
          <el-icon><Document /></el-icon>
          <span>请假管理</span>
        </el-menu-item>
        <el-menu-item index="/parent/albums">
          <el-icon><Picture /></el-icon>
          <span>成长相册</span>
        </el-menu-item>
        <el-menu-item index="/parent/health">
          <el-icon><DataLine /></el-icon>
          <span>健康档案</span>
        </el-menu-item>
        <el-menu-item index="/parent/profile">
          <el-icon><Setting /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-content">
          <span>欢迎，{{ userStore.userInfo.realName }}</span>
          <el-button @click="handleLogout" size="small">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { House, User, Bell, Calendar, Document, Picture, DataLine, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  })
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
  color: #fff;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
