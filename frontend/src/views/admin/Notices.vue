<template>
  <div class="notices-container">
    <h2>通知管理</h2>
    <el-card>
      <el-row style="margin-bottom: 20px">
        <el-button type="primary" @click="showAddDialog">发布通知</el-button>
      </el-row>

      <el-table :data="notices" style="width: 100%">
        <el-table-column prop="title" label="标题" width="250" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === '通知'" type="primary">通知</el-tag>
            <el-tag v-else-if="row.type === '公告'" type="success">公告</el-tag>
            <el-tag v-else type="warning">活动</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="readCount" label="阅读数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="viewNotice(row)">查看</el-button>
            <el-button size="small" @click="editNotice(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteNotice(row.id)">删除</el-button>
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

    <!-- 发布/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="通知标题">
          <el-input v-model="form.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="通知类型">
          <el-radio-group v-model="form.type">
            <el-radio label="通知">通知</el-radio>
            <el-radio label="公告">公告</el-radio>
            <el-radio label="活动">活动</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入通知内容"
          />
        </el-form-item>
        <el-form-item label="目标对象">
          <el-radio-group v-model="form.targetAudience">
            <el-radio label="全体">全体家长</el-radio>
            <el-radio label="班级">指定班级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择班级" v-if="form.targetAudience === '班级'">
          <el-select v-model="form.targetClassIds" multiple placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.className"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">保存为草稿</el-radio>
            <el-radio :label="1">立即发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNotice">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="通知详情" width="700px">
      <div class="notice-detail">
        <h3>{{ currentNotice.title }}</h3>
        <div class="notice-meta">
          <el-tag :type="currentNotice.type === '通知' ? 'primary' : currentNotice.type === '公告' ? 'success' : 'warning'">
            {{ currentNotice.type }}
          </el-tag>
          <span>发布人：{{ currentNotice.publisherName }}</span>
          <span>发布时间：{{ currentNotice.publishTime }}</span>
          <span>阅读数：{{ currentNotice.readCount }}</span>
        </div>
        <div class="notice-content">
          {{ currentNotice.content }}
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNoticeList, addNotice, updateNotice, deleteNotice as delNotice, getClassList } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const notices = ref([])
const classList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('发布通知')
const currentNotice = ref({})
const form = ref({
  title: '',
  type: '通知',
  content: '',
  targetAudience: '全体',
  targetClassIds: [],
  status: 1
})

const fetchNotices = async () => {
  try {
    const res = await getNoticeList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    notices.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取通知列表失败')
  }
}

const fetchClassList = async () => {
  try {
    const res = await getClassList({ pageNum: 1, pageSize: 100 })
    classList.value = res.data.records || []
  } catch (error) {
    console.error('获取班级列表失败')
  }
}

const showAddDialog = () => {
  dialogTitle.value = '发布通知'
  form.value = {
    title: '',
    type: '通知',
    content: '',
    targetAudience: '全体',
    targetClassIds: [],
    status: 1
  }
  dialogVisible.value = true
}

const editNotice = (row) => {
  dialogTitle.value = '编辑通知'
  form.value = {
    ...row,
    targetClassIds: row.targetClassIds || []
  }
  dialogVisible.value = true
}

const viewNotice = (row) => {
  currentNotice.value = row
  viewDialogVisible.value = true
}

const saveNotice = async () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写必填项')
    return
  }

  try {
    if (form.value.id) {
      await updateNotice(form.value.id, form.value)
      ElMessage.success('通知更新成功')
    } else {
      await addNotice(form.value)
      ElMessage.success('通知发布成功')
    }
    dialogVisible.value = false
    fetchNotices()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteNotice = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该通知吗？', '提示', {
      type: 'warning'
    })
    await delNotice(id)
    ElMessage.success('删除成功')
    fetchNotices()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchNotices()
  fetchClassList()
})
</script>

<style scoped>
.notices-container {
  padding: 20px;
}

.notice-detail h3 {
  margin-bottom: 15px;
  font-size: 20px;
  color: #333;
}

.notice-meta {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #666;
}

.notice-content {
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
}
</style>
