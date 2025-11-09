<template>
  <div class="feedback-container">
    <h2>意见反馈管理</h2>

    <el-card>
      <!-- 筛选条件 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable @change="fetchFeedback">
            <el-option label="待处理" value="pending" />
            <el-option label="已回复" value="replied" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchFeedback">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>

      <!-- 反馈列表 -->
      <el-table :data="feedbackList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待处理</el-tag>
            <el-tag v-else-if="row.status === 'replied'" type="success">已回复</el-tag>
            <el-tag v-else>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showReplyDialog(row)">回复</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchFeedback"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复反馈" width="600px">
      <div style="margin-bottom: 20px">
        <div style="color: #909399; font-size: 14px; margin-bottom: 8px">用户反馈：</div>
        <div style="padding: 12px; background: #f5f7fa; border-radius: 4px">{{ currentFeedback?.content }}</div>
      </div>
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.reply"
            type="textarea"
            :rows="5"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeedbackList, replyFeedback, deleteFeedback } from '@/api/admin'

const feedbackList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({
  status: ''
})

const replyDialogVisible = ref(false)
const currentFeedback = ref(null)
const replyForm = ref({
  reply: ''
})

// 获取反馈列表
const fetchFeedback = async () => {
  try {
    const res = await getFeedbackList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: searchForm.value.status
    })
    feedbackList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取反馈列表失败')
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = { status: '' }
  currentPage.value = 1
  fetchFeedback()
}

// 显示回复对话框
const showReplyDialog = (feedback) => {
  currentFeedback.value = feedback
  replyForm.value = {
    reply: feedback.reply || ''
  }
  replyDialogVisible.value = true
}

// 提交回复
const submitReply = async () => {
  if (!replyForm.value.reply) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    await replyFeedback(currentFeedback.value.id, replyForm.value.reply)
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchFeedback()
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

// 删除反馈
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条反馈吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteFeedback(id)
    ElMessage.success('删除成功')
    fetchFeedback()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchFeedback()
})
</script>

<style scoped>
.feedback-container {
  padding: 20px;
}
</style>
