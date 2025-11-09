<template>
  <div class="feedback-container">
    <h2>意见反馈</h2>

    <el-card>
      <!-- 提交反馈按钮 -->
      <el-row style="margin-bottom: 20px">
        <el-button type="primary" @click="showSubmitDialog">提交反馈</el-button>
      </el-row>

      <!-- 反馈列表 -->
      <el-table :data="feedbackList" style="width: 100%">
        <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待处理</el-tag>
            <el-tag v-else-if="row.status === 'replied'" type="success">已回复</el-tag>
            <el-tag v-else>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="提交时间" width="180" />
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
        @current-change="fetchFeedback"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 提交反馈对话框 -->
    <el-dialog v-model="submitDialogVisible" title="提交反馈" width="600px">
      <el-form :model="feedbackForm" label-width="100px">
        <el-form-item label="反馈内容">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="6"
            placeholder="请详细描述您的问题或建议"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFeedback">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="反馈详情" width="600px">
      <div>
        <div style="margin-bottom: 20px">
          <div style="color: #909399; font-size: 14px; margin-bottom: 8px">我的反馈：</div>
          <div style="padding: 12px; background: #f5f7fa; border-radius: 4px">
            {{ currentFeedback?.content }}
          </div>
          <div style="color: #909399; font-size: 12px; margin-top: 8px">
            提交时间：{{ currentFeedback?.createdTime }}
          </div>
        </div>

        <div v-if="currentFeedback?.reply" style="margin-bottom: 20px">
          <div style="color: #909399; font-size: 14px; margin-bottom: 8px">管理员回复：</div>
          <div style="padding: 12px; background: #e8f4ff; border-radius: 4px; border-left: 3px solid #409eff">
            {{ currentFeedback.reply }}
          </div>
          <div style="color: #909399; font-size: 12px; margin-top: 8px">
            回复时间：{{ currentFeedback?.replyTime }}
          </div>
        </div>

        <div v-else>
          <el-empty description="暂无回复" :image-size="80" />
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
import { getMyFeedbackList, submitFeedback as submitFeedbackApi, deleteFeedback } from '@/api/parent'

const feedbackList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const submitDialogVisible = ref(false)
const feedbackForm = ref({
  content: ''
})

const detailDialogVisible = ref(false)
const currentFeedback = ref(null)

// 获取反馈列表
const fetchFeedback = async () => {
  try {
    const res = await getMyFeedbackList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    feedbackList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取反馈列表失败')
  }
}

// 显示提交对话框
const showSubmitDialog = () => {
  feedbackForm.value = { content: '' }
  submitDialogVisible.value = true
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackForm.value.content) {
    ElMessage.warning('请输入反馈内容')
    return
  }

  try {
    await submitFeedbackApi(feedbackForm.value.content)
    ElMessage.success('提交成功')
    submitDialogVisible.value = false
    fetchFeedback()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

// 查看详情
const showDetail = (feedback) => {
  currentFeedback.value = feedback
  detailDialogVisible.value = true
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
