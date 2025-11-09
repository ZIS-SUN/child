<template>
  <div class="interaction-container">
    <h2>家园互动管理</h2>

    <el-card>
      <!-- 筛选条件 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-input v-model="searchForm.parentId" placeholder="家长ID" clearable />
        </el-col>
        <el-col :span="6">
          <el-input v-model="searchForm.childId" placeholder="幼儿ID" clearable />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="fetchInteraction">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>

      <!-- 未读统计 -->
      <el-alert
        v-if="unreadCount > 0"
        :title="`您有 ${unreadCount} 条未读消息`"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px"
      />

      <!-- 互动列表 -->
      <el-table :data="interactionList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="parentId" label="家长ID" width="100" />
        <el-table-column prop="childId" label="幼儿ID" width="100" />
        <el-table-column prop="message" label="留言内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isRead === 0" type="danger">未读</el-tag>
            <el-tag v-else type="success">已读</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="留言时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">查看</el-button>
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
        @current-change="fetchInteraction"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复家长" width="600px">
      <div style="margin-bottom: 20px">
        <div style="color: #909399; font-size: 14px; margin-bottom: 8px">家长留言：</div>
        <div style="padding: 12px; background: #f5f7fa; border-radius: 4px">{{ currentInteraction?.message }}</div>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="互动详情" width="600px">
      <div>
        <div style="margin-bottom: 20px">
          <div style="color: #909399; font-size: 14px; margin-bottom: 8px">家长留言：</div>
          <div style="padding: 12px; background: #f5f7fa; border-radius: 4px">
            {{ currentInteraction?.message }}
          </div>
          <div style="color: #909399; font-size: 12px; margin-top: 8px">
            留言时间：{{ currentInteraction?.createdTime }}
          </div>
        </div>

        <div v-if="currentInteraction?.reply" style="margin-bottom: 20px">
          <div style="color: #909399; font-size: 14px; margin-bottom: 8px">我的回复：</div>
          <div style="padding: 12px; background: #e8f4ff; border-radius: 4px; border-left: 3px solid #409eff">
            {{ currentInteraction.reply }}
          </div>
          <div style="color: #909399; font-size: 12px; margin-top: 8px">
            回复时间：{{ currentInteraction?.replyTime }}
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
import {
  getInteractionList,
  replyInteraction,
  markInteractionAsRead,
  deleteInteraction,
  getUnreadInteractionCount
} from '@/api/admin'

const interactionList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const unreadCount = ref(0)
const searchForm = ref({
  parentId: '',
  childId: ''
})

const replyDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentInteraction = ref(null)
const replyForm = ref({
  reply: ''
})

// 获取互动列表
const fetchInteraction = async () => {
  try {
    const res = await getInteractionList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      parentId: searchForm.value.parentId || undefined,
      childId: searchForm.value.childId || undefined
    })
    interactionList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取互动列表失败')
  }
}

// 获取未读数量
const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadInteractionCount()
    unreadCount.value = res.data.count
  } catch (error) {
    console.error('获取未读数量失败', error)
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = { parentId: '', childId: '' }
  currentPage.value = 1
  fetchInteraction()
}

// 显示回复对话框
const showReplyDialog = async (interaction) => {
  currentInteraction.value = interaction
  replyForm.value = {
    reply: interaction.reply || ''
  }
  replyDialogVisible.value = true

  // 标记为已读
  if (interaction.isRead === 0) {
    await markInteractionAsRead(interaction.id)
    fetchUnreadCount()
    fetchInteraction()
  }
}

// 提交回复
const submitReply = async () => {
  if (!replyForm.value.reply) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    await replyInteraction(currentInteraction.value.id, replyForm.value.reply)
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchInteraction()
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

// 查看详情
const showDetail = async (interaction) => {
  currentInteraction.value = interaction
  detailDialogVisible.value = true

  // 标记为已读
  if (interaction.isRead === 0) {
    await markInteractionAsRead(interaction.id)
    fetchUnreadCount()
    fetchInteraction()
  }
}

// 删除互动
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条互动记录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteInteraction(id)
    ElMessage.success('删除成功')
    fetchInteraction()
    fetchUnreadCount()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchInteraction()
  fetchUnreadCount()
})
</script>

<style scoped>
.interaction-container {
  padding: 20px;
}
</style>
