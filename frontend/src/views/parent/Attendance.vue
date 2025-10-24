<template>
  <div class="attendance-container">
    <h2>考勤记录</h2>

    <!-- 幼儿选择 -->
    <el-card style="margin-bottom: 20px">
      <el-select v-model="selectedChildId" placeholder="选择孩子" @change="onChildChange" style="width: 200px">
        <el-option
          v-for="child in children"
          :key="child.id"
          :label="child.name"
          :value="child.id"
        />
      </el-select>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px" v-if="stats.totalCount > 0">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">本月总天数</div>
            <div class="stats-value">{{ stats.totalCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card present">
          <div class="stats-content">
            <div class="stats-label">出勤天数</div>
            <div class="stats-value">{{ stats.presentCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card absent">
          <div class="stats-content">
            <div class="stats-label">缺勤天数</div>
            <div class="stats-value">{{ stats.absentCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card rate">
          <div class="stats-content">
            <div class="stats-label">出勤率</div>
            <div class="stats-value">{{ stats.attendanceRate }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 日历视图 -->
    <el-card>
      <div class="calendar-header">
        <el-button @click="prevMonth" icon="ArrowLeft" />
        <span class="current-month">{{ currentYear }}年{{ currentMonth }}月</span>
        <el-button @click="nextMonth" icon="ArrowRight" />
      </div>

      <div class="calendar">
        <div class="weekdays">
          <div class="weekday" v-for="day in ['日', '一', '二', '三', '四', '五', '六']" :key="day">
            {{ day }}
          </div>
        </div>
        <div class="days">
          <div
            v-for="day in calendarDays"
            :key="day.date"
            class="day"
            :class="{
              'other-month': day.otherMonth,
              'today': day.isToday,
              'has-record': day.record,
              'present': day.record?.status === 'present',
              'absent': day.record?.status === 'absent',
              'late': day.record?.status === 'late'
            }"
            @click="viewDayDetail(day)"
          >
            <div class="day-number">{{ day.day }}</div>
            <div class="day-status" v-if="day.record">
              <span v-if="day.record.status === 'present'" class="status-dot present"></span>
              <span v-else-if="day.record.status === 'absent'" class="status-dot absent"></span>
              <span v-else-if="day.record.status === 'late'" class="status-dot late"></span>
            </div>
          </div>
        </div>
      </div>

      <div class="legend">
        <div class="legend-item">
          <span class="legend-dot present"></span>
          <span>出勤</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot absent"></span>
          <span>缺勤</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot late"></span>
          <span>迟到</span>
        </div>
      </div>
    </el-card>

    <!-- 考勤详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="考勤详情" width="500px">
      <div v-if="selectedDay.record" class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="日期">
            {{ selectedDay.record.attendanceDate }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="selectedDay.record.status === 'present'" type="success">出勤</el-tag>
            <el-tag v-else-if="selectedDay.record.status === 'absent'" type="danger">缺勤</el-tag>
            <el-tag v-else-if="selectedDay.record.status === 'late'" type="warning">迟到</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="签到时间">
            {{ selectedDay.record.checkInTime || '未签到' }}
          </el-descriptions-item>
          <el-descriptions-item label="签退时间">
            {{ selectedDay.record.checkOutTime || '未签退' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注">
            {{ selectedDay.record.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-else class="no-record">
        该日期无考勤记录
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getMyChildren } from '@/api/parent'
import { getAttendanceCalendar, getAttendanceStats } from '@/api/parent'
import { ElMessage } from 'element-plus'

const children = ref([])
const selectedChildId = ref(null)
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)
const attendanceRecords = ref([])
const stats = ref({})
const detailDialogVisible = ref(false)
const selectedDay = ref({})

// 获取孩子列表
const fetchChildren = async () => {
  try {
    const res = await getMyChildren()
    children.value = res.data
    if (children.value.length > 0) {
      selectedChildId.value = children.value[0].id
      fetchAttendanceData()
    }
  } catch (error) {
    ElMessage.error('获取孩子信息失败')
  }
}

// 获取考勤数据
const fetchAttendanceData = async () => {
  if (!selectedChildId.value) return

  try {
    // 获取日历数据
    const calendarRes = await getAttendanceCalendar({
      childId: selectedChildId.value,
      year: currentYear.value,
      month: currentMonth.value
    })
    attendanceRecords.value = calendarRes.data

    // 获取统计数据
    const statsRes = await getAttendanceStats({
      childId: selectedChildId.value,
      year: currentYear.value,
      month: currentMonth.value
    })
    stats.value = statsRes.data
  } catch (error) {
    ElMessage.error('获取考勤数据失败')
  }
}

// 生成日历数据
const calendarDays = computed(() => {
  const days = []
  const firstDay = new Date(currentYear.value, currentMonth.value - 1, 1)
  const lastDay = new Date(currentYear.value, currentMonth.value, 0)
  const prevMonthLastDay = new Date(currentYear.value, currentMonth.value - 1, 0)

  // 上月天数
  const prevMonthDays = prevMonthLastDay.getDate()
  const startWeekDay = firstDay.getDay()

  for (let i = startWeekDay - 1; i >= 0; i--) {
    days.push({
      day: prevMonthDays - i,
      date: `${currentYear.value}-${String(currentMonth.value - 1).padStart(2, '0')}-${String(prevMonthDays - i).padStart(2, '0')}`,
      otherMonth: true,
      isToday: false,
      record: null
    })
  }

  // 本月天数
  const today = new Date()
  const recordsMap = {}
  attendanceRecords.value.forEach(record => {
    recordsMap[record.attendanceDate] = record
  })

  for (let i = 1; i <= lastDay.getDate(); i++) {
    const dateStr = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const isToday = today.getFullYear() === currentYear.value &&
                    today.getMonth() + 1 === currentMonth.value &&
                    today.getDate() === i

    days.push({
      day: i,
      date: dateStr,
      otherMonth: false,
      isToday,
      record: recordsMap[dateStr] || null
    })
  }

  // 下月天数补齐
  const remainingDays = 42 - days.length
  for (let i = 1; i <= remainingDays; i++) {
    days.push({
      day: i,
      date: `${currentYear.value}-${String(currentMonth.value + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`,
      otherMonth: true,
      isToday: false,
      record: null
    })
  }

  return days
})

// 切换孩子
const onChildChange = () => {
  fetchAttendanceData()
}

// 上一月
const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
  fetchAttendanceData()
}

// 下一月
const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
  fetchAttendanceData()
}

// 查看日期详情
const viewDayDetail = (day) => {
  if (day.otherMonth || !day.record) return
  selectedDay.value = day
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchChildren()
})
</script>

<style scoped>
.attendance-container {
  padding: 20px;
}

.stats-card {
  text-align: center;
}

.stats-content {
  padding: 10px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stats-card.present .stats-value {
  color: #67c23a;
}

.stats-card.absent .stats-value {
  color: #f56c6c;
}

.stats-card.rate .stats-value {
  color: #409eff;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.current-month {
  font-size: 18px;
  font-weight: bold;
}

.calendar {
  margin-bottom: 20px;
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background-color: #f5f7fa;
  padding: 10px 0;
}

.weekday {
  text-align: center;
  font-weight: bold;
  color: #606266;
}

.days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background-color: #f5f7fa;
}

.day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: white;
  cursor: pointer;
  position: relative;
  transition: all 0.3s;
}

.day:hover:not(.other-month) {
  background-color: #f5f7fa;
}

.day.other-month {
  color: #c0c4cc;
  cursor: default;
}

.day.today {
  background-color: #ecf5ff;
}

.day.has-record {
  font-weight: bold;
}

.day.present {
  border: 2px solid #67c23a;
}

.day.absent {
  border: 2px solid #f56c6c;
}

.day.late {
  border: 2px solid #e6a23c;
}

.day-number {
  font-size: 16px;
}

.day-status {
  margin-top: 4px;
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.present,
.legend-dot.present {
  background-color: #67c23a;
}

.status-dot.absent,
.legend-dot.absent {
  background-color: #f56c6c;
}

.status-dot.late,
.legend-dot.late {
  background-color: #e6a23c;
}

.legend {
  display: flex;
  justify-content: center;
  gap: 30px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.detail-content {
  padding: 10px 0;
}

.no-record {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  font-size: 14px;
}
</style>
