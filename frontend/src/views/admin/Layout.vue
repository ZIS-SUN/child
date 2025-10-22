<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="logo-container">
        <transition name="fade">
          <div v-if="!isCollapse" class="logo-content">
            <div class="logo-icon">🎓</div>
            <h3 class="logo-text">UltraThink</h3>
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
.layout-container {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  background: linear-gradient(180deg, #1e2a3a 0%, #2d3e50 100%);
  transition: width 0.3s;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  margin-bottom: 20px;
}

.logo-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 32px;
  animation: bounce 2s infinite;
}

.logo-text {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-collapsed {
  font-size: 28px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.sidebar-menu {
  border: none;
}

.sidebar-menu :deep(.el-menu-item) {
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 主内容区 */
.main-container {
  background: #f5f7fa;
}

/* 顶部导航 */
.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 20px;
  color: #606266;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.username:hover {
  color: #409eff;
}

/* 内容区 */
.content {
  padding: 24px;
  overflow-y: auto;
  min-height: calc(100vh - 64px);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
