<template>
  <div class="dashboard-container">
    <h2>数据面板</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="50" color="#409EFF"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalChildren }}</div>
              <div class="stat-label">幼儿总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="50" color="#67C23A"><School /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalClasses }}</div>
              <div class="stat-label">班级总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="50" color="#E6A23C"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.attendanceRate }}%</div>
              <div class="stat-label">今日出勤率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="50" color="#F56C6C"><Document /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingLeave }}</div>
              <div class="stat-label">待审批请假</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboard } from '@/api/admin'
import { ElMessage } from 'element-plus'

const stats = ref({
  totalChildren: 0,
  totalClasses: 0,
  attendanceRate: 0,
  pendingLeave: 0,
  unreadMessages: 0
})

const fetchDashboard = async () => {
  try {
    const res = await getDashboard()
    stats.value = res.data
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

onMounted(() => {
  fetchDashboard()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 8px;
}
</style>
