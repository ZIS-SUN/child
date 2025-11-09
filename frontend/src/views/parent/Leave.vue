<template>
  <div class="leave-container">
    <h2>请假管理</h2>

    <!-- 提交请假 -->
    <el-card style="margin-bottom: 20px">
      <template #header>
        <span>提交请假申请</span>
      </template>
      <el-form :model="leaveForm" label-width="100px">
        <el-form-item label="孩子">
          <el-select v-model="leaveForm.childId" placeholder="请选择孩子" style="width: 100%">
            <el-option
              v-for="child in children"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="请假类型">
          <el-radio-group v-model="leaveForm.leaveType">
            <el-radio label="病假">病假</el-radio>
            <el-radio label="事假">事假</el-radio>
            <el-radio label="其他">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="leaveForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="leaveForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="请假原因">
          <el-input
            v-model="leaveForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入请假原因"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitLeave">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 请假记录 -->
    <el-card>
      <template #header>
        <span>请假记录</span>
      </template>

      <!-- 筛选 -->
      <el-row style="margin-bottom: 20px">
        <el-select v-model="queryParams.status" placeholder="全部状态" style="width: 150px; margin-right: 10px" @change="fetchLeaveList">
          <el-option label="全部状态" value="" />
          <el-option label="待审批" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
          <el-option label="已撤销" value="CANCELLED" />
        </el-select>
      </el-row>

      <el-table :data="leaveList" style="width: 100%">
        <el-table-column prop="childName" label="孩子姓名" width="100" />
        <el-table-column prop="leaveType" label="请假类型" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="reason" label="请假原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PENDING'" type="warning">待审批</el-tag>
            <el-tag v-else-if="row.status === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
            <el-tag v-else type="info">已撤销</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approverName" label="审批人" width="100" />
        <el-table-column prop="approveRemark" label="审批备注" width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="danger"
              @click="cancelLeave(row.id)"
            >
              撤销
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @current-change="fetchLeaveList"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyChildren } from '@/api/parent'
import { submitLeaveRequest, getLeaveList, cancelLeaveRequest } from '@/api/parent'
import { ElMessage, ElMessageBox } from 'element-plus'

const children = ref([])
const leaveList = ref([])
const total = ref(0)

const leaveForm = ref({
  childId: '',
  leaveType: '病假',
  startDate: '',
  endDate: '',
  reason: ''
})

const queryParams = ref({
  status: '',
  pageNum: 1,
  pageSize: 10
})

const fetchChildren = async () => {
  try {
    const res = await getMyChildren()
    children.value = res.data || []
    if (children.value.length > 0) {
      leaveForm.value.childId = children.value[0].id
    }
  } catch (error) {
    ElMessage.error('获取孩子列表失败')
  }
}

const fetchLeaveList = async () => {
  try {
    const res = await getLeaveList()
    leaveList.value = res.data || []
    total.value = leaveList.value.length
  } catch (error) {
    ElMessage.error('获取请假记录失败')
  }
}

const submitLeave = async () => {
  if (!leaveForm.value.childId) {
    ElMessage.warning('请选择孩子')
    return
  }
  if (!leaveForm.value.startDate || !leaveForm.value.endDate) {
    ElMessage.warning('请选择请假时间')
    return
  }
  if (!leaveForm.value.reason) {
    ElMessage.warning('请输入请假原因')
    return
  }

  try {
    await submitLeaveRequest(leaveForm.value)
    ElMessage.success('请假申请提交成功')
    resetForm()
    fetchLeaveList()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const cancelLeave = async (id) => {
  try {
    await ElMessageBox.confirm('确定要撤销该请假申请吗？', '提示', {
      type: 'warning'
    })
    await cancelLeaveRequest(id)
    ElMessage.success('撤销成功')
    fetchLeaveList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤销失败')
    }
  }
}

const resetForm = () => {
  leaveForm.value = {
    childId: children.value.length > 0 ? children.value[0].id : '',
    leaveType: '病假',
    startDate: '',
    endDate: '',
    reason: ''
  }
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
</style>
