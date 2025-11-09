<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="logo-container">
        <transition name="fade">
          <div v-if="!isCollapse" class="logo-content">
            <div class="logo-icon">🎓</div>
            <h3 class="logo-text">幼儿园管理</h3>
          </div>
          <div v-else class="logo-collapsed">
            🎓
          </div>
        </transition>
      </div>

      <el-menu
        :default-active="$route.path"
        :collapse="isCollapse"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#c0c4cc"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/admin/dashboard" class="menu-item">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>数据面板</template>
        </el-menu-item>
        <el-menu-item index="/admin/users" class="menu-item">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/children" class="menu-item">
          <el-icon><Avatar /></el-icon>
          <template #title>幼儿管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/classes" class="menu-item">
          <el-icon><School /></el-icon>
          <template #title>班级管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/attendance" class="menu-item">
          <el-icon><Calendar /></el-icon>
          <template #title>考勤管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/leave" class="menu-item">
          <el-icon><DocumentChecked /></el-icon>
          <template #title>请假审批</template>
        </el-menu-item>
        <el-menu-item index="/admin/albums" class="menu-item">
          <el-icon><Picture /></el-icon>
          <template #title>成长相册</template>
        </el-menu-item>
        <el-menu-item index="/admin/health" class="menu-item">
          <el-icon><DataLine /></el-icon>
          <template #title>健康管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/notices" class="menu-item">
          <el-icon><Bell /></el-icon>
          <template #title>通知管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            class="collapse-btn"
            :icon="isCollapse ? 'Expand' : 'Fold'"
            @click="toggleCollapse"
            text
          >
            <el-icon v-if="isCollapse"><Expand /></el-icon>
            <el-icon v-else><Fold /></el-icon>
          </el-button>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <div class="user-info">
            <el-avatar :size="36" src="https://ui-avatars.com/api/?name=Admin&background=667eea&color=fff" />
            <el-dropdown>
              <span class="username">
                管理员 <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/public/home')">
                    <el-icon><House /></el-icon>
                    前台首页
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  DataAnalysis,
  User,
  Avatar,
  School,
  Bell,
  Calendar,
  DocumentChecked,
  Picture,
  DataLine,
  Expand,
  Fold,
  ArrowDown,
  House,
  SwitchButton
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const currentPageName = computed(() => {
  const map = {
    '/admin/dashboard': '数据面板',
    '/admin/users': '用户管理',
    '/admin/children': '幼儿管理',
    '/admin/classes': '班级管理',
    '/admin/attendance': '考勤管理',
    '/admin/leave': '请假审批',
    '/admin/albums': '成长相册',
    '/admin/health': '健康管理',
    '/admin/notices': '通知管理'
  }
  return map[route.path] || '管理后台'
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    ElMessage.success('退出登录成功')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
/* ========== 鸿蒙风格管理后台布局 ========== */

.layout-container {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  background: linear-gradient(180deg, #1D1D1F 0%, #2C2C2E 100%);
  transition: width 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 100;
}

.sidebar::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: rgba(255, 255, 255, 0.08);
}

.logo-container {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.25);
  margin-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-content {
  display: flex;
  align-items: center;
  gap: 12px;
  animation: fadeIn 0.6s ease;
}

.logo-icon {
  font-size: 36px;
  animation: bounce 3s infinite ease-in-out;
  filter: drop-shadow(0 2px 8px rgba(0, 125, 255, 0.4));
}

.logo-text {
  color: #fff;
  font-size: 19px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
}

.logo-collapsed {
  font-size: 32px;
  animation: bounce 3s infinite ease-in-out;
  filter: drop-shadow(0 2px 8px rgba(0, 125, 255, 0.4));
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.sidebar-menu {
  border: none;
  padding: 0 8px;
}

.sidebar-menu :deep(.el-menu-item) {
  margin: 4px 0;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  color: #EBEBF0;
  height: 48px;
  line-height: 48px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.12) !important;
  color: #FFFFFF;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 100%) !important;
  color: #FFFFFF;
  box-shadow: 0 4px 16px rgba(0, 125, 255, 0.35);
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

/* 主内容区 */
.main-container {
  background: #F5F5F9;
}

/* 顶部导航 */
.header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  z-index: 99;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.collapse-btn {
  font-size: 22px;
  color: #86868B;
  transition: all 0.3s ease;
}

.collapse-btn:hover {
  color: #007DFF;
  transform: scale(1.1);
}

.breadcrumb {
  font-size: 15px;
}

.breadcrumb :deep(.el-breadcrumb__inner) {
  color: #1D1D1F;
  font-weight: 500;
}

.breadcrumb :deep(.el-breadcrumb__inner.is-link) {
  color: #86868B;
}

.breadcrumb :deep(.el-breadcrumb__inner.is-link:hover) {
  color: #007DFF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info :deep(.el-avatar) {
  border: 2px solid #F5F5F9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.user-info :deep(.el-avatar:hover) {
  border-color: #007DFF;
  transform: scale(1.05);
}

.username {
  cursor: pointer;
  color: #1D1D1F;
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s ease;
  letter-spacing: 0.3px;
}

.username:hover {
  color: #007DFF;
}

.username :deep(.el-icon) {
  transition: transform 0.3s ease;
}

.username:hover :deep(.el-icon) {
  transform: rotate(180deg);
}

/* Dropdown样式 */
:deep(.el-dropdown-menu) {
  border-radius: 16px;
  padding: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

:deep(.el-dropdown-menu__item) {
  border-radius: 12px;
  padding: 10px 16px;
  margin: 2px 0;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background: rgba(0, 125, 255, 0.1);
  color: #007DFF;
}

/* 内容区 */
.content {
  padding: 24px;
  overflow-y: auto;
  min-height: calc(100vh - 64px);
  background: #F5F5F9;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-transform-enter-active {
  animation: slideInRight 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-transform-leave-active {
  animation: slideOutLeft 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideOutLeft {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(-30px);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }

  .header-left {
    gap: 16px;
  }

  .content {
    padding: 16px;
  }
}
</style>
