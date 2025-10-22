<template>
  <div class="notices-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">通知公告</h1>
        <p class="page-subtitle">及时了解幼儿园最新动态</p>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索公告标题或内容"
          :prefix-icon="Search"
          size="large"
          clearable
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 公告列表 -->
      <div class="notices-container" v-loading="loading">
        <el-empty v-if="noticeList.length === 0" description="暂无公告" :image-size="200" />
        <div v-else class="notices-list">
          <div
            class="notice-card"
            v-for="notice in noticeList"
            :key="notice.id"
            @click="handleViewDetail(notice.id)"
          >
            <div class="notice-badge" v-if="isNew(notice.publishTime)">
              <el-icon><Bell /></el-icon>
              <span>最新</span>
            </div>
            <div class="notice-header">
              <h2 class="notice-title">{{ notice.title }}</h2>
              <div class="notice-meta">
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(notice.publishTime) }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ notice.views || 0 }} 次浏览
                </span>
              </div>
            </div>
            <div class="notice-content" v-if="notice.summary">
              {{ notice.summary }}
            </div>
            <div class="notice-footer">
              <el-button type="primary" link>
                查看详情 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 30, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNoticeList } from '@/api/public'
import { ElMessage } from 'element-plus'
import { Search, Clock, View, Bell, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const noticeList = ref([
  {
    id: 1,
    title: '关于2024年秋季招生工作的通知',
    summary: '为做好2024年秋季招生工作，现将有关事项通知如下...',
    publishTime: '2024-10-20 10:00:00',
    views: 156
  },
  {
    id: 2,
    title: '本周五举办家长开放日活动',
    summary: '为增进家园沟通，让家长更好地了解幼儿在园情况，本园将于本周五举办家长开放日活动...',
    publishTime: '2024-10-21 14:30:00',
    views: 89
  },
  {
    id: 3,
    title: '国庆节放假通知及注意事项',
    summary: '根据国务院办公厅通知，现将国庆节放假安排通知如下...',
    publishTime: '2024-10-18 09:00:00',
    views: 245
  },
  {
    id: 4,
    title: '关于加强秋冬季传染病防控工作的通知',
    summary: '秋冬季是传染病高发季节，为保障全园师生身体健康...',
    publishTime: '2024-10-15 11:20:00',
    views: 178
  },
  {
    id: 5,
    title: '幼儿园环境改造升级通知',
    summary: '为给孩子们提供更好的学习和生活环境，本园将进行环境改造升级...',
    publishTime: '2024-10-10 16:45:00',
    views: 132
  }
])

const fetchNotices = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
      keyword: searchKeyword.value,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    noticeList.value = res.data.records || res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取公告列表失败', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchNotices()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchNotices()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchNotices()
}

const handleViewDetail = (id) => {
  router.push(`/public/notices/${id}`)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr
}

const isNew = (dateStr) => {
  if (!dateStr) return false
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  return diff < 3 * 24 * 60 * 60 * 1000 // 3天内
}

onMounted(() => {
  fetchNotices()
})
</script>

<style scoped>
.notices-page {
  min-height: 100vh;
  padding: 40px 0;
  background: linear-gradient(to bottom, #f8f9fe 0%, #ffffff 100%);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 48px;
}

.page-title {
  font-size: 42px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 12px;
}

.page-subtitle {
  font-size: 16px;
  color: #7f8c8d;
}

.search-bar {
  margin-bottom: 32px;
}

.search-input {
  max-width: 600px;
  margin: 0 auto;
}

.search-input :deep(.el-input-group__append) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.notices-container {
  min-height: 400px;
}

.notices-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.notice-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.notice-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 0;
  background: linear-gradient(to bottom, #667eea, #764ba2);
  transition: height 0.3s;
}

.notice-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.notice-card:hover::before {
  height: 100%;
}

.notice-badge {
  position: absolute;
  top: 24px;
  right: 24px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.notice-header {
  margin-bottom: 16px;
}

.notice-title {
  font-size: 22px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #95a5a6;
}

.notice-content {
  font-size: 15px;
  color: #5a6c7d;
  line-height: 1.8;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notice-footer {
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 48px;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }

  .notice-card {
    padding: 24px;
  }

  .notice-title {
    font-size: 18px;
  }

  .notice-badge {
    position: static;
    display: inline-flex;
    margin-bottom: 12px;
  }
}
</style>
