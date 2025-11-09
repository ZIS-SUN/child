<template>
  <div class="public-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <span class="logo-icon">🎓</span>
          <span class="logo-text">幼儿园信息管理系统</span>
        </div>
        <nav class="nav">
          <router-link to="/public/home" class="nav-item">首页</router-link>
          <router-link to="/public/notices" class="nav-item">通知公告</router-link>
          <router-link to="/public/menu" class="nav-item">每周食谱</router-link>
          <router-link to="/public/teachers" class="nav-item">师资风采</router-link>
          <router-link to="/public/albums" class="nav-item">园所相册</router-link>
          <router-link to="/public/enrollment" class="nav-item nav-item-highlight">在线报名</router-link>
        </nav>
        <div class="header-actions">
          <el-button type="primary" @click="$router.push('/login')">登录系统</el-button>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>联系我们</h3>
            <p><el-icon><Location /></el-icon> 地址：某市某区某街道123号</p>
            <p><el-icon><Phone /></el-icon> 电话：0123-12345678</p>
            <p><el-icon><Message /></el-icon> 邮箱：contact@ultrathink.com</p>
          </div>
          <div class="footer-section">
            <h3>开园时间</h3>
            <p>周一至周五：07:30 - 17:30</p>
            <p>周六日及法定节假日休息</p>
          </div>
          <div class="footer-section">
            <h3>关注我们</h3>
            <div class="social-links">
              <el-button circle><el-icon><ChatDotRound /></el-icon></el-button>
              <el-button circle><el-icon><Picture /></el-icon></el-button>
              <el-button circle><el-icon><Share /></el-icon></el-button>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 幼儿园信息管理系统. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { Location, Phone, Message, ChatDotRound, Picture, Share } from '@element-plus/icons-vue'
</script>

<style scoped>
/* ========== 鸿蒙风格公共布局 ========== */

.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #F5F5F9;
}

/* Header */
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: #1D1D1F;
  cursor: pointer;
  user-select: none;
  transition: all 0.3s ease;
}

.logo:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 36px;
  animation: bounce 3s infinite ease-in-out;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

.logo-text {
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
}

.nav {
  display: flex;
  gap: 6px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  padding: 10px 20px;
  color: #1D1D1F;
  text-decoration: none;
  border-radius: 14px;
  font-weight: 500;
  font-size: 15px;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  letter-spacing: 0.3px;
}

.nav-item:hover {
  background: #F5F5F9;
  color: #007DFF;
}

.nav-item.router-link-active {
  color: #007DFF;
  background: rgba(0, 125, 255, 0.1);
}

.nav-item-highlight {
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 100%);
  color: white !important;
  box-shadow: 0 4px 12px rgba(0, 125, 255, 0.25);
}

.nav-item-highlight:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 125, 255, 0.35);
  color: white !important;
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 100%);
}

.nav-item-highlight.router-link-active {
  background: linear-gradient(135deg, #007DFF 0%, #0066CC 100%);
}

.header-actions :deep(.el-button) {
  border-radius: 14px;
  padding: 10px 24px;
  font-weight: 500;
  font-size: 15px;
  letter-spacing: 0.3px;
}

/* Main Content */
.main-content {
  flex: 1;
  background: #F5F5F9;
}

/* Footer */
.footer {
  background: linear-gradient(180deg, #1D1D1F 0%, #2C2C2E 100%);
  color: white;
  padding: 48px 0 24px;
  margin-top: 48px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 48px;
  margin-bottom: 32px;
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 20px;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: #FFFFFF;
}

.footer-section p {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  opacity: 0.85;
  font-size: 15px;
  line-height: 1.6;
  color: #EBEBF0;
}

.footer-section p :deep(.el-icon) {
  font-size: 16px;
  color: #007DFF;
}

.social-links {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.social-links :deep(.el-button) {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: #FFFFFF;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.social-links :deep(.el-button:hover) {
  background: #007DFF;
  border-color: #007DFF;
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 125, 255, 0.3);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 24px;
  text-align: center;
  opacity: 0.6;
  font-size: 14px;
  letter-spacing: 0.3px;
}

/* Responsive */
@media (max-width: 768px) {
  .header-container {
    height: auto;
    flex-wrap: wrap;
    padding: 16px 20px;
  }

  .logo {
    font-size: 18px;
  }

  .logo-icon {
    font-size: 32px;
  }

  .nav {
    order: 3;
    width: 100%;
    margin-top: 12px;
    overflow-x: auto;
    gap: 6px;
    justify-content: flex-start;
    padding-bottom: 4px;
  }

  .nav::-webkit-scrollbar {
    height: 3px;
  }

  .nav-item {
    white-space: nowrap;
    padding: 8px 16px;
    font-size: 14px;
  }

  .header-actions {
    margin-left: auto;
  }

  .header-actions :deep(.el-button) {
    padding: 8px 20px;
    font-size: 14px;
  }

  .footer {
    padding: 40px 0 20px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }
}
</style>
