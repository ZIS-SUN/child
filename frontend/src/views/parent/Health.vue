<template>
  <div class="health-container">
    <h2>健康档案</h2>

    <el-card>
      <!-- 孩子选择 -->
      <el-row :gutter="20" style="margin-bottom: 20px" v-if="childrenList.length > 1">
        <el-col :span="8">
          <el-select
            v-model="selectedChildId"
            placeholder="选择孩子"
            @change="loadHealthData"
            style="width: 100%"
          >
            <el-option
              v-for="child in childrenList"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-select>
        </el-col>
      </el-row>

      <div v-if="selectedChildId">
        <!-- 基本健康信息 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <span class="card-title">基本健康信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="血型">
              <el-tag>{{ healthInfo.bloodType || '未填写' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="过敏史" :span="2">
              {{ healthInfo.allergyInfo || '无' }}
            </el-descriptions-item>
            <el-descriptions-item label="病史" :span="2">
              {{ healthInfo.medicalHistory || '无' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 最近7天体温 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <span class="card-title">最近7天体温记录</span>
          </template>
          <el-table :data="recentTemperature" style="width: 100%">
            <el-table-column prop="measureTime" label="测量时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.measureTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="temperature" label="体温(°C)" width="120">
              <template #default="{ row }">
                <el-tag :type="getTempType(row.temperature)" size="large">
                  {{ row.temperature }}°C
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="period" label="时段" width="100">
              <template #default="{ row }">
                {{ getPeriodText(row.period) }}
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="recentTemperature.length === 0" description="暂无体温记录" />
        </el-card>

        <!-- 成长曲线 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <span class="card-title">成长记录</span>
          </template>

          <!-- 统计卡片 -->
          <el-row :gutter="20" style="margin-bottom: 20px" v-if="growthList.length > 0">
            <el-col :span="12">
              <el-card class="stats-card">
                <div class="stats-content">
                  <div class="stats-label">最新身高</div>
                  <div class="stats-value">{{ latestGrowth.height || '-' }} cm</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card class="stats-card">
                <div class="stats-content">
                  <div class="stats-label">最新体重</div>
                  <div class="stats-value">{{ latestGrowth.weight || '-' }} kg</div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 成长记录表格 -->
          <el-table :data="growthList" style="width: 100%">
            <el-table-column prop="measureDate" label="测量日期" width="150" />
            <el-table-column prop="height" label="身高(cm)" width="120">
              <template #default="{ row }">
                <el-tag type="success" v-if="row.height">{{ row.height }} cm</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="体重(kg)" width="120">
              <template #default="{ row }">
                <el-tag type="warning" v-if="row.weight">{{ row.weight }} kg</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="growthList.length === 0" description="暂无成长记录" />
        </el-card>
      </div>

      <el-empty v-else description="暂无孩子信息" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMyChildren,
  getChildHealthInfo,
  getRecentTemperature,
  getChildGrowthRecords
} from '@/api/parent'

// 数据
const childrenList = ref([])
const selectedChildId = ref(null)
const healthInfo = ref({})
const recentTemperature = ref([])
const growthList = ref([])

// 计算最新成长数据
const latestGrowth = computed(() => {
  if (growthList.value.length === 0) return {}
  return growthList.value[growthList.value.length - 1]
})

// 获取孩子列表
const fetchChildren = async () => {
  try {
    const res = await getMyChildren()
    childrenList.value = res.data || []
    if (childrenList.value.length > 0) {
      selectedChildId.value = childrenList.value[0].id
      loadHealthData()
    }
  } catch (error) {
    ElMessage.error('获取孩子列表失败')
  }
}

// 加载健康数据
const loadHealthData = async () => {
  if (!selectedChildId.value) return

  try {
    // 获取健康基本信息
    const healthRes = await getChildHealthInfo(selectedChildId.value)
    healthInfo.value = healthRes.data || {}

    // 获取最近7天体温
    const tempRes = await getRecentTemperature(selectedChildId.value)
    recentTemperature.value = tempRes.data || []

    // 获取成长记录
    const growthRes = await getChildGrowthRecords(selectedChildId.value)
    growthList.value = growthRes.data || []
  } catch (error) {
    ElMessage.error('加载健康数据失败')
  }
}

// 体温状态
const getTempType = (temp) => {
  if (temp < 36) return 'info'
  if (temp >= 37.3) return 'danger'
  if (temp >= 37) return 'warning'
  return 'success'
}

// 时段文本
const getPeriodText = (period) => {
  const map = {
    morning: '早上',
    noon: '中午',
    afternoon: '下午',
    evening: '晚上'
  }
  return map[period] || period
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

onMounted(() => {
  fetchChildren()
})
</script>

<style scoped>
.health-container {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
}

.info-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.stats-card {
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stats-content {
  padding: 20px;
}

.stats-label {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.stats-value {
  font-size: 32px;
  font-weight: bold;
}
</style>
