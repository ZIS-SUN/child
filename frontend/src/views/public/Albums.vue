<template>
  <div class="albums-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">园所相册</h1>
        <p class="page-subtitle">记录成长 · 分享快乐</p>
      </div>

      <!-- 专题筛选 -->
      <div class="category-filter">
        <el-button
          v-for="cat in categories"
          :key="cat.value"
          :type="selectedCategory === cat.value ? 'primary' : ''"
          @click="selectedCategory = cat.value"
          size="large"
        >
          {{ cat.icon }} {{ cat.label }}
        </el-button>
      </div>

      <!-- 相册瀑布流 -->
      <div class="albums-container" v-loading="loading">
        <div class="masonry-grid">
          <div
            class="album-item"
            v-for="(photo, index) in filteredPhotos"
            :key="index"
            @click="handlePreview(index)"
          >
            <img :src="photo.url" :alt="photo.title">
            <div class="album-overlay">
              <h3>{{ photo.title }}</h3>
              <p>{{ photo.date }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 图片预览 -->
      <el-image-viewer
        v-if="showViewer"
        :url-list="filteredPhotos.map(p => p.url)"
        :initial-index="currentIndex"
        @close="showViewer = false"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAlbumList } from '@/api/public'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const selectedCategory = ref('all')
const showViewer = ref(false)
const currentIndex = ref(0)

const categories = [
  { label: '全部', value: 'all', icon: '📷' },
  { label: '精彩活动', value: 'activity', icon: '🎉' },
  { label: '教室环境', value: 'classroom', icon: '🏫' },
  { label: '户外操场', value: 'playground', icon: '⚽' },
  { label: '美食时刻', value: 'meal', icon: '🍱' }
]

const photoList = ref([
  { id: 1, title: '春游活动', category: 'activity', date: '2024-04-15', url: 'https://via.placeholder.com/400x300/667eea/ffffff?text=春游活动' },
  { id: 2, title: '温馨教室', category: 'classroom', date: '2024-03-20', url: 'https://via.placeholder.com/400x500/764ba2/ffffff?text=温馨教室' },
  { id: 3, title: '户外游戏', category: 'playground', date: '2024-05-10', url: 'https://via.placeholder.com/400x400/f093fb/ffffff?text=户外游戏' },
  { id: 4, title: '午餐时间', category: 'meal', date: '2024-06-05', url: 'https://via.placeholder.com/400x350/4facfe/ffffff?text=午餐时间' },
  { id: 5, title: '运动会', category: 'activity', date: '2024-05-20', url: 'https://via.placeholder.com/400x450/00f2fe/ffffff?text=运动会' },
  { id: 6, title: '阅读角', category: 'classroom', date: '2024-04-08', url: 'https://via.placeholder.com/400x300/43e97b/ffffff?text=阅读角' },
  { id: 7, title: '滑滑梯', category: 'playground', date: '2024-06-15', url: 'https://via.placeholder.com/400x500/38f9d7/ffffff?text=滑滑梯' },
  { id: 8, title: '营养早餐', category: 'meal', date: '2024-06-20', url: 'https://via.placeholder.com/400x350/fa709a/ffffff?text=营养早餐' }
])

const filteredPhotos = computed(() => {
  if (selectedCategory.value === 'all') {
    return photoList.value
  }
  return photoList.value.filter(p => p.category === selectedCategory.value)
})

const handlePreview = (index) => {
  currentIndex.value = index
  showViewer.value = true
}

onMounted(() => {
  // fetchAlbums()
})
</script>

<style scoped>
.albums-page {
  min-height: 100vh;
  padding: 40px 0;
  background: linear-gradient(to bottom, #f8f9fe 0%, #ffffff 100%);
}

.container {
  max-width: 1200px;
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

.category-filter {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.masonry-grid {
  column-count: 3;
  column-gap: 20px;
}

.album-item {
  break-inside: avoid;
  margin-bottom: 20px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.album-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.album-item img {
  width: 100%;
  display: block;
  transition: transform 0.3s;
}

.album-item:hover img {
  transform: scale(1.05);
}

.album-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  padding: 20px;
  color: white;
  transform: translateY(100%);
  transition: transform 0.3s;
}

.album-item:hover .album-overlay {
  transform: translateY(0);
}

.album-overlay h3 {
  font-size: 16px;
  margin-bottom: 4px;
}

.album-overlay p {
  font-size: 13px;
  opacity: 0.9;
}

@media (max-width: 1024px) {
  .masonry-grid {
    column-count: 2;
  }
}

@media (max-width: 768px) {
  .masonry-grid {
    column-count: 1;
  }
}
</style>
