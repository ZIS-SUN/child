<template>
  <div class="interaction-container">
    <h2>家园互动</h2>

    <el-card>
      <!-- 发起互动按钮 -->
      <el-row style="margin-bottom: 20px">
        <el-button type="primary" @click="showCreateDialog">给老师留言</el-button>
      </el-row>

      <!-- 筛选条件 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="8">
          <el-select v-model="searchForm.childId" placeholder="选择孩子" clearable @change="fetchInteraction">
            <el-option
              v-for="child in childrenList"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-select>
        </el-col>
      </el-row>

      <!-- 互动列表 -->
      <el-table :data="interactionList" style="width: 100%">
        <el-table-column label="孩子" width="120">
          <template #default="{ row }">
            {{ getChildName(row.childId) }}
          </template>
        </el-table-column>
        <el-table-column prop="message" label="我的留言" min-width="200" show-overflow-tooltip />
        <el-table-column label="回复状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.reply" type="success">已回复</el-tag>
            <el-tag v-else type="info">待回复</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="留言时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">查看</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchInteraction"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 发起互动对话框 -->
    <el-dialog v-model="createDialogVisible" title="给老师留言" width="600px">
      <el-form :model="interactionForm" label-width="100px">
        <el-form-item label="选择孩子">
          <el-select v-model="interactionForm.childId" placeholder="请选择孩子">
            <el-option
              v-for="child in childrenList"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择老师">
          <el-select v-model="interactionForm.teacherId" placeholder="请选择老师">
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.realName"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="留言内容">
          <el-input
            v-model="interactionForm.message"
            type="textarea"
            :rows="6"
            placeholder="请输入您想对老师说的话"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInteraction">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="互动详情" width="600px">
      <div>
        <div style="margin-bottom: 20px">
          <div style="color: #909399; font-size: 14px; margin-bottom: 8px">我的留言：</div>
          <div style="padding: 12px; background: #f5f7fa; border-radius: 4px">
            {{ currentInteraction?.message }}
          </div>
          <div style="color: #909399; font-size: 12px; margin-top: 8px">
            留言时间：{{ currentInteraction?.createdTime }}
          </div>
        </div>

        <div v-if="currentInteraction?.reply" style="margin-bottom: 20px">
          <div style="color: #909399; font-size: 14px; margin-bottom: 8px">老师回复：</div>
          <div style="padding: 12px; background: #e8f4ff; border-radius: 4px; border-left: 3px solid #409eff">
            {{ currentInteraction.reply }}
          </div>
          <div style="color: #909399; font-size: 12px; margin-top: 8px">
            回复时间：{{ currentInteraction?.replyTime }}
          </div>
        </div>

        <div v-else>
          <el-empty description="老师还未回复" :image-size="80" />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyChildren } from '@/api/parent'
import { getMyInteractionList, createInteraction, deleteInteraction } from '@/api/parent'
import { getUserList } from '@/api/admin'

const interactionList = ref([])
const childrenList = ref([])
const teacherList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({
  childId: ''
})

const createDialogVisible = ref(false)
const interactionForm = ref({
  childId: null,
  teacherId: null,
  message: ''
})

const detailDialogVisible = ref(false)
const currentInteraction = ref(null)

// 获取孩子列表
const fetchChildren = async () => {
  try {
    const res = await getMyChildren()
    childrenList.value = res.data
  } catch (error) {
    ElMessage.error('获取孩子列表失败')
  }
}

// 获取老师列表
const fetchTeachers = async () => {
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 100, role: 'TEACHER' })
    teacherList.value = res.data.records
  } catch (error) {
    ElMessage.error('获取老师列表失败')
  }
}

// 获取互动列表
const fetchInteraction = async () => {
  try {
    const res = await getMyInteractionList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      childId: searchForm.value.childId || undefined
    })
    interactionList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取互动列表失败')
  }
}

// 显示发起互动对话框
const showCreateDialog = () => {
  interactionForm.value = {
    childId: childrenList.value[0]?.id,
    teacherId: null,
    message: ''
  }
  createDialogVisible.value = true
}

// 提交互动
const submitInteraction = async () => {
  if (!interactionForm.value.childId) {
    ElMessage.warning('请选择孩子')
    return
  }
  if (!interactionForm.value.teacherId) {
    ElMessage.warning('请选择老师')
    return
  }
  if (!interactionForm.value.message) {
    ElMessage.warning('请输入留言内容')
    return
  }

  try {
    await createInteraction(interactionForm.value)
    ElMessage.success('留言成功')
    createDialogVisible.value = false
    fetchInteraction()
  } catch (error) {
    ElMessage.error('留言失败')
  }
}

// 查看详情
const showDetail = (interaction) => {
  currentInteraction.value = interaction
  detailDialogVisible.value = true
}

// 删除互动
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条留言吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteInteraction(id)
    ElMessage.success('删除成功')
    fetchInteraction()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 获取孩子姓名
const getChildName = (childId) => {
  const child = childrenList.value.find(c => c.id === childId)
  return child ? child.name : '未知'
}

onMounted(() => {
  fetchChildren()
  fetchTeachers()
  fetchInteraction()
})
</script>

<style scoped>
.interaction-container {
  padding: 20px;
}
</style>
