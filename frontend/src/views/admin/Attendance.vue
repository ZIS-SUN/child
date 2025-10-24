<template>
  <div class="attendance-container">
    <h2>考勤管理</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-label">应到人数</div>
            <div class="stats-value">{{ classStats.totalChildren || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card present">
          <div class="stats-content">
            <div class="stats-label">实到人数</div>
            <div class="stats-value">{{ classStats.presentCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card absent">
          <div class="stats-content">
            <div class="stats-label">缺勤人数</div>
            <div class="stats-value">{{ classStats.absentCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card rate">
          <div class="stats-content">
            <div class="stats-label">出勤率</div>
            <div class="stats-value">{{ classStats.attendanceRate || '0%' }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <!-- 查询条件 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-select v-model="searchForm.classId" placeholder="选择班级" clearable @change="fetchStats">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            @change="fetchStats"
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="searchForm.status" placeholder="考勤状态" clearable>
            <el-option label="出勤" value="present" />
            <el-option label="缺勤" value="absent" />
            <el-option label="迟到" value="late" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchAttendance">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>

      <!-- 快捷操作 -->
      <el-row style="margin-bottom: 20px">
        <el-col :span="12">
          <el-button type="success" @click="showCheckInDialog">签到</el-button>
          <el-button type="warning" @click="showAbsentDialog">记录缺勤</el-button>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" @click="exportData">导出数据</el-button>
        </el-col>
      </el-row>

      <!-- 考勤记录表格 -->
      <el-table :data="attendanceList" style="width: 100%">
        <el-table-column prop="childId" label="幼儿ID" width="80" />
        <el-table-column label="幼儿姓名" width="120">
          <template #default="{ row }">
            {{ getChildName(row.childId) }}
          </template>
        </el-table-column>
        <el-table-column prop="attendanceDate" label="日期" width="120" />
        <el-table-column label="签到时间" width="120">
          <template #default="{ row }">
            {{ row.checkInTime ? formatTime(row.checkInTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="签退时间" width="120">
          <template #default="{ row }">
            {{ row.checkOutTime ? formatTime(row.checkOutTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'present'" type="success">出勤</el-tag>
            <el-tag v-else-if="row.status === 'absent'" type="danger">缺勤</el-tag>
            <el-tag v-else-if="row.status === 'late'" type="warning">迟到</el-tag>
            <el-tag v-else>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="editAttendance(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRecord(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchAttendance"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 签到对话框 -->
    <el-dialog v-model="checkInDialogVisible" title="幼儿签到" width="500px">
      <el-form :model="checkInForm" label-width="100px">
        <el-form-item label="选择幼儿">
          <el-select v-model="checkInForm.childId" placeholder="请选择幼儿" filterable>
            <el-option
              v-for="child in childrenList"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkInForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCheckIn">确定</el-button>
      </template>
    </el-dialog>

    <!-- 记录缺勤对话框 -->
    <el-dialog v-model="absentDialogVisible" title="记录缺勤" width="500px">
      <el-form :model="absentForm" label-width="100px">
        <el-form-item label="选择幼儿">
          <el-select v-model="absentForm.childId" placeholder="请选择幼儿" filterable>
            <el-option
              v-for="child in childrenList"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="缺勤日期">
          <el-date-picker
            v-model="absentForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="absentForm.remark" type="textarea" :rows="3" placeholder="缺勤原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="absentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAbsent">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑考勤对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑考勤记录" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="签到时间">
          <el-date-picker
            v-model="editForm.checkInTime"
            type="datetime"
            placeholder="选择签到时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="签退时间">
          <el-date-picker
            v-model="editForm.checkOutTime"
            type="datetime"
            placeholder="选择签退时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="出勤" value="present" />
            <el-option label="缺勤" value="absent" />
            <el-option label="迟到" value="late" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  getAttendanceList,
  checkIn,
  checkOut,
  recordAbsent,
  updateAttendance,
  deleteAttendance,
  getClassAttendanceStats,
  getClassList,
  getChildList
} from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exportToExcel, formatDateTime } from '@/utils/export'

const attendanceList = ref([])
const classList = ref([])
const childrenList = ref([])
const childrenMap = ref({})
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const classStats = ref({})
const searchForm = ref({
  classId: null,
  date: new Date().toISOString().split('T')[0],
  status: ''
})

const checkInDialogVisible = ref(false)
const checkInForm = ref({
  childId: null,
  remark: ''
})

const absentDialogVisible = ref(false)
const absentForm = ref({
  childId: null,
  date: new Date().toISOString().split('T')[0],
  remark: ''
})

const editDialogVisible = ref(false)
const editForm = ref({
  id: null,
  checkInTime: '',
  checkOutTime: '',
  status: '',
  remark: ''
})

// 获取考勤记录
const fetchAttendance = async () => {
  try {
    const res = await getAttendanceList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      classId: searchForm.value.classId,
      startDate: searchForm.value.date,
      endDate: searchForm.value.date,
      status: searchForm.value.status
    })
    attendanceList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取考勤记录失败')
  }
}

// 获取班级列表
const fetchClasses = async () => {
  try {
    const res = await getClassList({ pageNum: 1, pageSize: 100 })
    classList.value = res.data.records
    if (classList.value.length > 0 && !searchForm.value.classId) {
      searchForm.value.classId = classList.value[0].id
      fetchStats()
    }
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  }
}

// 获取幼儿列表
const fetchChildren = async () => {
  try {
    const res = await getChildList({ pageNum: 1, pageSize: 1000 })
    childrenList.value = res.data.records
    // 创建幼儿ID到姓名的映射
    childrenList.value.forEach(child => {
      childrenMap.value[child.id] = child.name
    })
  } catch (error) {
    ElMessage.error('获取幼儿列表失败')
  }
}

// 获取班级考勤统计
const fetchStats = async () => {
  if (!searchForm.value.classId) return
  try {
    const res = await getClassAttendanceStats(searchForm.value.classId, searchForm.value.date)
    classStats.value = res.data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 显示签到对话框
const showCheckInDialog = () => {
  checkInForm.value = { childId: null, remark: '' }
  checkInDialogVisible.value = true
}

// 处理签到
const handleCheckIn = async () => {
  if (!checkInForm.value.childId) {
    ElMessage.warning('请选择幼儿')
    return
  }
  try {
    await checkIn(checkInForm.value)
    ElMessage.success('签到成功')
    checkInDialogVisible.value = false
    fetchAttendance()
    fetchStats()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '签到失败')
  }
}

// 显示缺勤对话框
const showAbsentDialog = () => {
  absentForm.value = {
    childId: null,
    date: new Date().toISOString().split('T')[0],
    remark: ''
  }
  absentDialogVisible.value = true
}

// 处理记录缺勤
const handleAbsent = async () => {
  if (!absentForm.value.childId) {
    ElMessage.warning('请选择幼儿')
    return
  }
  try {
    await recordAbsent(absentForm.value)
    ElMessage.success('缺勤记录成功')
    absentDialogVisible.value = false
    fetchAttendance()
    fetchStats()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '记录失败')
  }
}

// 编辑考勤
const editAttendance = (row) => {
  editForm.value = {
    id: row.id,
    checkInTime: row.checkInTime,
    checkOutTime: row.checkOutTime,
    status: row.status,
    remark: row.remark
  }
  editDialogVisible.value = true
}

// 保存编辑
const saveEdit = async () => {
  try {
    const { id, ...data } = editForm.value
    await updateAttendance(id, data)
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchAttendance()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 删除记录
const deleteRecord = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条考勤记录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAttendance(id)
    ElMessage.success('删除成功')
    fetchAttendance()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    classId: classList.value[0]?.id,
    date: new Date().toISOString().split('T')[0],
    status: ''
  }
  fetchAttendance()
  fetchStats()
}

// 获取幼儿姓名
const getChildName = (childId) => {
  return childrenMap.value[childId] || '未知'
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  return timeStr.split(' ')[1] || timeStr
}

// 导出数据
const exportData = () => {
  if (attendanceList.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  const columns = [
    { label: '幼儿姓名', prop: 'childId', formatter: (row) => getChildName(row.childId) },
    { label: '考勤日期', prop: 'date' },
    { label: '签到时间', prop: 'checkInTime', formatter: (row) => row.checkInTime || '-' },
    { label: '签退时间', prop: 'checkOutTime', formatter: (row) => row.checkOutTime || '-' },
    { label: '状态', prop: 'status', formatter: (row) => {
      const statusMap = { present: '出勤', absent: '缺勤', late: '迟到' }
      return statusMap[row.status] || row.status
    }},
    { label: '备注', prop: 'remark', formatter: (row) => row.remark || '-' }
  ]

  const filename = `考勤记录_${new Date().toISOString().split('T')[0]}.xls`
  exportToExcel(attendanceList.value, columns, filename)
  ElMessage.success('导出成功')
}

onMounted(() => {
  fetchClasses()
  fetchChildren()
  fetchAttendance()
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
</style>
