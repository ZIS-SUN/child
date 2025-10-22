<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h2 class="page-title">数据面板</h2>
      <p class="page-subtitle">欢迎回来，这是您的工作台概览</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card stat-card-blue" @click="$router.push('/admin/children')">
          <div class="stat-icon">
            <el-icon :size="48"><Avatar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalChildren }}</div>
            <div class="stat-label">幼儿总数</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>本月新增 12 人</span>
            </div>
          </div>
          <div class="stat-bg-icon">👶</div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card stat-card-green" @click="$router.push('/admin/classes')">
          <div class="stat-icon">
            <el-icon :size="48"><School /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalClasses }}</div>
            <div class="stat-label">班级总数</div>
            <div class="stat-trend">
              <el-icon><SuccessFilled /></el-icon>
              <span>全部运行中</span>
            </div>
          </div>
          <div class="stat-bg-icon">🏫</div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card stat-card-orange">
          <div class="stat-icon">
            <el-icon :size="48"><DataLine /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.attendanceRate }}%</div>
            <div class="stat-label">今日出勤率</div>
            <div class="stat-trend">
              <el-icon><Top /></el-icon>
              <span>较昨日 +2.5%</span>
            </div>
          </div>
          <div class="stat-bg-icon">📊</div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card stat-card-red" @click="$router.push('/admin/notices')">
          <div class="stat-icon">
            <el-icon :size="48"><DocumentCopy /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.pendingLeave }}</div>
            <div class="stat-label">待审批请假</div>
            <div class="stat-trend">
              <el-icon><Warning /></el-icon>
              <span>需要处理</span>
            </div>
          </div>
          <div class="stat-bg-icon">📝</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表和列表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><DataLine /></el-icon>
                出勤趋势
              </span>
              <el-button type="primary" text>查看详情</el-button>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="100" color="#dcdfe6"><DataAnalysis /></el-icon>
            <p>图表区域 - 可接入 ECharts</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="quick-actions-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Management /></el-icon>
                快捷操作
              </span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/admin/users')">
              <el-icon class="action-icon" color="#409eff"><UserFilled /></el-icon>
              <span>添加用户</span>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
            <div class="action-item" @click="$router.push('/admin/children')">
              <el-icon class="action-icon" color="#67c23a"><Avatar /></el-icon>
              <span>添加幼儿</span>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
            <div class="action-item" @click="$router.push('/admin/classes')">
              <el-icon class="action-icon" color="#e6a23c"><School /></el-icon>
              <span>添加班级</span>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
            <div class="action-item" @click="$router.push('/admin/notices')">
              <el-icon class="action-icon" color="#f56c6c"><Bell /></el-icon>
              <span>发布通知</span>
              <el-icon class="action-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新动态 -->
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="activity-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Clock /></el-icon>
                最新动态
              </span>
              <el-button type="primary" text>查看全部</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in recentActivities"
              :key="index"
              :timestamp="activity.time"
              :color="activity.color"
            >
              <div class="activity-content">
                <span class="activity-title">{{ activity.title }}</span>
                <span class="activity-desc">{{ activity.description }}</span>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/admin'
import { ElMessage } from 'element-plus'
import {
  Avatar,
  School,
  DataLine,
  DocumentCopy,
  TrendCharts,
  SuccessFilled,
  Top,
  Warning,
  DataAnalysis,
  Management,
  UserFilled,
  Bell,
  ArrowRight,
  Clock
} from '@element-plus/icons-vue'

const stats = ref({
  totalChildren: 156,
  totalClasses: 8,
  attendanceRate: 95.8,
  pendingLeave: 3,
  unreadMessages: 5
})

const recentActivities = ref([
  {
    title: '新生入园',
    description: '张小明家长完成了入园登记',
    time: '2小时前',
    color: '#409eff'
  },
  {
    title: '请假审批',
    description: '李老师审批通过了王小花的请假申请',
    time: '5小时前',
    color: '#67c23a'
  },
  {
    title: '通知发布',
    description: '园长发布了"家长会通知"',
    time: '1天前',
    color: '#e6a23c'
  },
  {
    title: '班级调整',
    description: '中班2班新增2名幼儿',
    time: '2天前',
    color: '#909399'
  }
])

const fetchDashboard = async () => {
  try {
    const res = await getDashboard()
    stats.value = res.data
  } catch (error) {
    console.error('获取数据失败', error)
    // ElMessage.error('获取数据失败')
  }
}

onMounted(() => {
  fetchDashboard()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  height: 160px;
  display: flex;
  gap: 20px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-card-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-card-green {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: #fff;
}

.stat-card-orange {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: #fff;
}

.stat-card-red {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-icon {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 12px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  opacity: 0.85;
}

.stat-bg-icon {
  position: absolute;
  right: -10px;
  bottom: -10px;
  font-size: 120px;
  opacity: 0.1;
}

/* 图表区域 */
.charts-row {
  margin-bottom: 24px;
}

.chart-card,
.quick-actions-card,
.activity-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  background: #fafafa;
  border-radius: 8px;
}

.chart-placeholder p {
  margin-top: 16px;
  font-size: 14px;
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #ecf5ff;
  transform: translateX(4px);
}

.action-icon {
  font-size: 24px;
}

.action-item span {
  flex: 1;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.action-arrow {
  color: #c0c4cc;
  transition: transform 0.3s;
}

.action-item:hover .action-arrow {
  transform: translateX(4px);
  color: #409eff;
}

/* 最新动态 */
.activity-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.activity-desc {
  font-size: 13px;
  color: #909399;
}

:deep(.el-timeline-item__timestamp) {
  color: #909399;
  font-size: 13px;
}
</style>
