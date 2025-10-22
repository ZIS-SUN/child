<template>
  <div class="home-container">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1 class="welcome-title">
          <span class="wave">👋</span> 欢迎回来
        </h1>
        <p class="welcome-subtitle">让我们一起关注孩子的成长与快乐</p>
      </div>
      <div class="welcome-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card-1" @click="$router.push('/parent/children')">
          <div class="stat-icon">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ childrenCount }}</div>
            <div class="stat-label">我的孩子</div>
          </div>
          <div class="stat-background">
            <div class="stat-shape"></div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card-2" @click="$router.push('/parent/notices')">
          <div class="stat-icon">
            <el-icon :size="32"><Bell /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ noticesCount }}</div>
            <div class="stat-label">未读通知</div>
          </div>
          <div class="stat-badge" v-if="noticesCount > 0">
            <span>NEW</span>
          </div>
          <div class="stat-background">
            <div class="stat-shape"></div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card-3" @click="$router.push('/parent/leave')">
          <div class="stat-icon">
            <el-icon :size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ leaveCount }}</div>
            <div class="stat-label">待审批请假</div>
          </div>
          <div class="stat-background">
            <div class="stat-shape"></div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-card-4">
          <div class="stat-icon">
            <el-icon :size="32"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ attendanceRate }}%</div>
            <div class="stat-label">本月出勤率</div>
          </div>
          <div class="stat-background">
            <div class="stat-shape"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card class="notice-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="card-title">
            <el-icon class="title-icon"><BellFilled /></el-icon>
            <span>最新公告</span>
          </div>
          <el-button type="primary" link @click="$router.push('/parent/notices')">
            查看更多 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <div v-loading="loading">
        <el-empty v-if="notices.length === 0" description="暂无公告" :image-size="120">
          <template #image>
            <div class="empty-icon">📢</div>
          </template>
        </el-empty>
        <div v-else class="notice-list">
          <div
            v-for="notice in notices"
            :key="notice.id"
            class="notice-item"
            @click="handleNoticeClick(notice)"
          >
            <div class="notice-content">
              <div class="notice-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="notice-main">
                <div class="notice-title">{{ notice.title }}</div>
                <div class="notice-time">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatTime(notice.publishTime) }}</span>
                </div>
              </div>
            </div>
            <el-icon class="notice-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHomeData } from '@/api/parent'
import { ElMessage } from 'element-plus'
import {
  User,
  Bell,
  BellFilled,
  Document,
  Calendar,
  Clock,
  ArrowRight
} from '@element-plus/icons-vue'

const childrenCount = ref(0)
const noticesCount = ref(0)
const leaveCount = ref(0)
const attendanceRate = ref(0)
const notices = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getHomeData()
    childrenCount.value = res.data.childrenCount || 0
    noticesCount.value = res.data.unreadNotices || 0
    leaveCount.value = res.data.pendingLeave || 0
    attendanceRate.value = res.data.attendanceRate || 0
    notices.value = res.data.recentNotices || []
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return time.split(' ')[0]
}

const handleNoticeClick = (notice) => {
  ElMessage.info('查看公告详情')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.home-container {
  padding: 24px;
  background: linear-gradient(to bottom, #f8f9fe 0%, #ffffff 100%);
  min-height: calc(100vh - 60px);
}

/* Welcome Banner */
.welcome-banner {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 40px 50px;
  margin-bottom: 30px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.welcome-content {
  position: relative;
  z-index: 2;
  color: white;
}

.welcome-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 10px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.wave {
  display: inline-block;
  animation: wave 2s infinite;
  transform-origin: 70% 70%;
}

@keyframes wave {
  0%, 100% { transform: rotate(0deg); }
  10%, 30% { transform: rotate(14deg); }
  20% { transform: rotate(-8deg); }
  40% { transform: rotate(-4deg); }
  50% { transform: rotate(10deg); }
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.95;
  margin: 0;
  font-weight: 400;
}

.welcome-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  opacity: 0.15;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: white;
}

.circle-1 {
  width: 200px;
  height: 200px;
  right: -50px;
  top: -50px;
}

.circle-2 {
  width: 150px;
  height: 150px;
  right: 100px;
  bottom: -30px;
}

.circle-3 {
  width: 100px;
  height: 100px;
  right: -20px;
  bottom: 50px;
}

/* Stats Row */
.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  position: relative;
  padding: 28px 24px;
  border-radius: 16px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background: white;
  display: flex;
  align-items: center;
  gap: 16px;
  height: 120px;
}

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.stat-card-1 {
  border-left: 4px solid #409EFF;
}

.stat-card-1 .stat-icon {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  color: white;
}

.stat-card-2 {
  border-left: 4px solid #67C23A;
}

.stat-card-2 .stat-icon {
  background: linear-gradient(135deg, #67C23A, #85ce61);
  color: white;
}

.stat-card-3 {
  border-left: 4px solid #E6A23C;
}

.stat-card-3 .stat-icon {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
  color: white;
}

.stat-card-4 {
  border-left: 4px solid #F56C6C;
}

.stat-card-4 .stat-icon {
  background: linear-gradient(135deg, #F56C6C, #f78989);
  color: white;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
  z-index: 2;
}

.stat-info {
  flex: 1;
  z-index: 2;
  position: relative;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.stat-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  z-index: 2;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 107, 107, 0.7);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 0 8px rgba(255, 107, 107, 0);
  }
}

.stat-background {
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 100px;
  height: 100px;
  opacity: 0.05;
  z-index: 1;
}

.stat-shape {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: currentColor;
}

/* Notice Card */
.notice-card {
  border-radius: 16px;
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.notice-card :deep(.el-card__header) {
  background: linear-gradient(to right, #f8f9fe, #ffffff);
  border-bottom: 2px solid #f0f2f5;
  padding: 20px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.title-icon {
  font-size: 22px;
  color: #667eea;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 16px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 0;
  background: linear-gradient(to bottom, #667eea, #764ba2);
  transition: height 0.3s;
}

.notice-item:hover {
  background: linear-gradient(to right, #f8f9fe, transparent);
  transform: translateX(4px);
}

.notice-item:hover::before {
  height: 100%;
}

.notice-content {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.notice-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.notice-main {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 15px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #95a5a6;
  font-size: 13px;
}

.notice-time .el-icon {
  font-size: 14px;
}

.notice-arrow {
  color: #bdc3c7;
  font-size: 16px;
  transition: all 0.3s;
  flex-shrink: 0;
}

.notice-item:hover .notice-arrow {
  color: #667eea;
  transform: translateX(4px);
}

/* Responsive */
@media (max-width: 768px) {
  .home-container {
    padding: 16px;
  }

  .welcome-banner {
    padding: 30px 24px;
  }

  .welcome-title {
    font-size: 28px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .stat-card {
    height: auto;
    padding: 20px;
    margin-bottom: 12px;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
  }

  .stat-value {
    font-size: 28px;
  }

  .notice-item {
    padding: 16px;
  }

  .notice-title {
    font-size: 14px;
  }
}
</style>
