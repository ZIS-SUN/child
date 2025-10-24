<template>
  <div class="leave-container">
    <h2>请假审批</h2>

    <el-card>
      <!-- 查询条件 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-select v-model="searchForm.status" placeholder="请假状态" clearable>
            <el-option label="待审批" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已撤销" value="cancelled" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchLeaveList">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-label">待审批</div>
              <div class="stats-value pending">{{ stats.pending || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-label">已通过</div>
              <div class="stats-value approved">{{ stats.approved || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-label">已拒绝</div>
              <div class="stats-value rejected">{{ stats.rejected || 0 }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 请假记录表格 -->
      <el-table :data="leaveList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="幼儿姓名" width="120">
          <template #default="{ row }">
            {{ getChildName(row.childId) }}
          </template>
        </el-table-column>
        <el-table-column label="请假类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.leaveType === 'sick'" type="warning">病假</el-tag>
            <el-tag v-else-if="row.leaveType === 'personal'" type="info">事假</el-tag>
            <el-tag v-else>{{ row.leaveType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="请假天数" width="100">
          <template #default="{ row }">
            {{ calculateDays(row.startTime, row.endTime) }}天
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 'rejected'" type="danger">已拒绝</el-tag>
            <el-tag v-else-if="row.status === 'cancelled'" type="info">已撤销</el-tag>
            <el-tag v-else>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批备注" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.approvalRemark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div v-if="row.status === 'pending'">
              <el-button size="small" type="success" @click="handleApprove(row)">通过</el-button>
              <el-button size="small" type="danger" @click="handleReject(row)">拒绝</el-button>
            </div>
            <span v-else class="text-muted">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchLeaveList"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="approvalDialogVisible" :title="approvalType === 'approve' ? '审批通过' : '审批拒绝'" width="500px">
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="审批意见">
          <el-input
            v-model="approvalForm.remark"
            type="textarea"
            :rows="4"
            :placeholder="approvalType === 'approve' ? '请输入审批意见（可选）' : '请输入拒绝原因'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button :type="approvalType === 'approve' ? 'success' : 'danger'" @click="submitApproval">
          确定{{ approvalType === 'approve' ? '通过' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getLeaveList, approveLeave, rejectLeave, getChildList } from '@/api/admin'
import { ElMessage } from 'element-plus'

const leaveList = ref([])
const childrenMap = ref({})
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  status: ''
})

const approvalDialogVisible = ref(false)
const approvalType = ref('approve') // 'approve' 或 'reject'
const approvalForm = ref({
  id: null,
  remark: ''
})

// 统计数据
const stats = computed(() => {
  const pending = leaveList.value.filter(item => item.status === 'pending').length
  const approved = leaveList.value.filter(item => item.status === 'approved').length
  const rejected = leaveList.value.filter(item => item.status === 'rejected').length
  return { pending, approved, rejected }
})

// 获取请假列表
const fetchLeaveList = async () => {
  try {
    const res = await getLeaveList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: searchForm.value.status
    })
    leaveList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取请假记录失败')
  }
}

// 获取幼儿列表
const fetchChildren = async () => {
  try {
    const res = await getChildList({ pageNum: 1, pageSize: 1000 })
    res.data.records.forEach(child => {
      childrenMap.value[child.id] = child.name
    })
  } catch (error) {
    console.error('获取幼儿列表失败', error)
  }
}

// 获取幼儿姓名
const getChildName = (childId) => {
  return childrenMap.value[childId] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  return dateTimeStr.replace('T', ' ')
}

// 计算请假天数
const calculateDays = (startTime, endTime) => {
  if (!startTime || !endTime) return 0
  const start = new Date(startTime)
  const end = new Date(endTime)
  const diff = Math.ceil((end - start) / (1000 * 60 * 60 * 24))
  return diff > 0 ? diff : 1
}

// 处理审批通过
const handleApprove = (row) => {
  approvalType.value = 'approve'
  approvalForm.value = {
    id: row.id,
    remark: ''
  }
  approvalDialogVisible.value = true
}

// 处理审批拒绝
const handleReject = (row) => {
  approvalType.value = 'reject'
  approvalForm.value = {
    id: row.id,
    remark: ''
  }
  approvalDialogVisible.value = true
}

// 提交审批
const submitApproval = async () => {
  try {
    if (approvalType.value === 'approve') {
      await approveLeave(approvalForm.value.id, approvalForm.value.remark)
      ElMessage.success('审批通过成功')
    } else {
      if (!approvalForm.value.remark) {
        ElMessage.warning('请输入拒绝原因')
        return
      }
      await rejectLeave(approvalForm.value.id, approvalForm.value.remark)
      ElMessage.success('已拒绝该请假申请')
    }
    approvalDialogVisible.value = false
    fetchLeaveList()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = { status: '' }
  currentPage.value = 1
  fetchLeaveList()
}

onMounted(() => {
  fetchChildren()
  fetchLeaveList()
})
</script>

<style scoped>
.leave-container {
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
}

.stats-value.pending {
  color: #e6a23c;
}

.stats-value.approved {
  color: #67c23a;
}

.stats-value.rejected {
  color: #f56c6c;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}
</style>
