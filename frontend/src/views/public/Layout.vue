<template>
  <div class="public-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <span class="logo-icon">🎓</span>
          <span class="logo-text">UltraThink 幼儿园</span>
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
          <p>&copy; 2024 UltraThink 幼儿园. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { Location, Phone, Message, ChatDotRound, Picture, Share } from '@element-plus/icons-vue'
</script>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Header */
.header {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  cursor: pointer;
}

.logo-icon {
  font-size: 32px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.logo-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav {
  display: flex;
  gap: 8px;
}

.nav-item {
  padding: 8px 20px;
  color: #2c3e50;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
  position: relative;
}

.nav-item:hover {
  background: #f0f2f5;
  color: #667eea;
}

.nav-item.router-link-active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.nav-item-highlight {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white !important;
}

.nav-item-highlight:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.nav-item-highlight.router-link-active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* Main Content */
.main-content {
  flex: 1;
  background: #f8f9fe;
}

/* Footer */
.footer {
  background: #2c3e50;
  color: white;
  padding: 40px 0 20px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: 600;
}

.footer-section p {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.social-links {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 20px;
  text-align: center;
  opacity: 0.7;
}

/* Responsive */
@media (max-width: 768px) {
  .header-container {
    height: auto;
    flex-wrap: wrap;
    padding: 12px 20px;
  }

  .nav {
    order: 3;
    width: 100%;
    margin-top: 12px;
    overflow-x: auto;
    gap: 4px;
  }

  .nav-item {
    white-space: nowrap;
    padding: 6px 16px;
    font-size: 14px;
  }

  .header-actions {
    margin-left: auto;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}
</style>
