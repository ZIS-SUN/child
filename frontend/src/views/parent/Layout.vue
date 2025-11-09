<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <span class="logo-icon">👨‍👩‍👧‍👦</span>
        <h3 class="logo-text">家长端</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="transparent"
        text-color="#c0c4cc"
        active-text-color="#ffffff"
        class="parent-menu"
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
/* ========== 鸿蒙风格家长端布局 ========== */

.layout-container {
  height: 100vh;
}

.el-aside {
  background: linear-gradient(180deg, #1D1D1F 0%, #2C2C2E 100%);
  color: #fff;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  position: relative;
}

.el-aside::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: rgba(255, 255, 255, 0.08);
}

.logo {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: rgba(0, 0, 0, 0.25);
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-icon {
  font-size: 28px;
  animation: bounce 3s infinite ease-in-out;
  filter: drop-shadow(0 2px 8px rgba(0, 204, 136, 0.4));
}

.logo-text {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  background: linear-gradient(135deg, #00CC88 0%, #00AA73 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

.el-header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  padding: 0 28px;
  height: 64px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.header-content > span {
  font-size: 15px;
  font-weight: 500;
  color: #1D1D1F;
  letter-spacing: 0.3px;
}

.header-content :deep(.el-button) {
  border-radius: 14px;
  padding: 8px 20px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.header-content :deep(.el-button--small) {
  font-size: 14px;
}

.el-main {
  background-color: #F5F5F9;
  padding: 24px;
  overflow-y: auto;
}

/* 菜单样式统一 */
.parent-menu {
  border: none;
  padding: 8px;
}

.parent-menu :deep(.el-menu-item) {
  margin: 4px 0;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  color: #EBEBF0;
  height: 48px;
  line-height: 48px;
}

.parent-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.12) !important;
  color: #FFFFFF;
}

.parent-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #00CC88 0%, #00AA73 100%) !important;
  color: #FFFFFF;
  box-shadow: 0 4px 16px rgba(0, 204, 136, 0.35);
}

.parent-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

/* 响应式 */
@media (max-width: 768px) {
  .el-header {
    padding: 0 16px;
  }

  .el-main {
    padding: 16px;
  }
}
</style>
