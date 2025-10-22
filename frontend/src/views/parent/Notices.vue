<template>
  <div class="notices-container">
    <h2>通知公告</h2>
    <el-card>
      <el-table :data="notices" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchNotices"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="通知详情"
      width="600px"
      @close="handleDialogClose"
    >
      <div v-if="currentNotice" class="notice-detail">
        <h3>{{ currentNotice.title }}</h3>
        <div class="notice-meta">
          <span>发布时间: {{ currentNotice.publishTime }}</span>
        </div>
        <el-divider />
        <div class="notice-content" v-html="currentNotice.content"></div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNoticeList, getNoticeDetail, markNoticeAsRead } from '@/api/parent'
import { ElMessage } from 'element-plus'

const notices = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentNotice = ref(null)

const fetchNotices = async () => {
  try {
    const res = await getNoticeList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    notices.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取通知失败')
  }
}

const viewDetail = async (id) => {
  try {
    const res = await getNoticeDetail(id)
    currentNotice.value = res.data
    detailDialogVisible.value = true

    // 标记为已读
    await markNoticeAsRead(id).catch(() => {})
  } catch (error) {
    ElMessage.error('获取通知详情失败')
  }
}

const handleDialogClose = () => {
  currentNotice.value = null
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped>
.notices-container {
  padding: 20px;
}

.notice-detail h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 10px;
}

.notice-meta {
  color: #909399;
  font-size: 14px;
  margin-bottom: 15px;
}

.notice-content {
  line-height: 1.8;
  color: #606266;
  font-size: 15px;
  white-space: pre-wrap;
}
</style>
