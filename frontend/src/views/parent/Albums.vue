<template>
  <div class="albums-container">
    <h2>成长相册</h2>

    <el-card>
      <!-- 相册网格 -->
      <el-row :gutter="20">
        <el-col :span="8" v-for="album in albumList" :key="album.id">
          <el-card class="album-card" :body-style="{ padding: '0' }" @click="viewAlbum(album)">
            <img
              :src="album.coverUrl || '/default-album.jpg'"
              class="album-cover"
            />
            <div class="album-info">
              <div class="album-title">{{ album.title }}</div>
              <div class="album-meta">
                <el-icon><Calendar /></el-icon>
                <span>{{ formatDate(album.createdAt) }}</span>
              </div>
              <div class="album-meta">
                <el-icon><Picture /></el-icon>
                <span>{{ album.photoCount || 0 }}张照片</span>
              </div>
              <div class="album-description" v-if="album.description">
                {{ album.description }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="albumList.length === 0" description="还没有相册哦~" />
    </el-card>

    <!-- 查看相册对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="currentAlbum?.title"
      width="90%"
      top="5vh"
      @close="closeDialog"
    >
      <div class="album-header" v-if="currentAlbum">
        <div class="album-info-detail">
          <p class="album-desc">{{ currentAlbum.description }}</p>
          <p class="album-time">创建时间: {{ formatDate(currentAlbum.createdAt) }}</p>
        </div>
      </div>

      <!-- 照片瀑布流 -->
      <div class="photo-waterfall">
        <div
          v-for="(photo, index) in currentPhotos"
          :key="index"
          class="photo-item"
          @click="previewPhoto(index)"
        >
          <el-image
            :src="photo.url"
            fit="cover"
            class="photo-image"
            lazy
          >
            <template #placeholder>
              <div class="image-slot">
                加载中...
              </div>
            </template>
            <template #error>
              <div class="image-slot">
                <el-icon><icon-picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="photo-overlay">
            <el-button type="primary" circle @click.stop="downloadPhoto(photo)">
              <el-icon><Download /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="currentPhotos.length === 0" description="暂无照片" />
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="showViewer"
      :url-list="photoUrls"
      :initial-index="currentPhotoIndex"
      @close="showViewer = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Picture, Download } from '@element-plus/icons-vue'
import { getMyAlbums, getAlbumPhotos } from '@/api/parent'
import { ElImageViewer } from 'element-plus'

// 数据
const albumList = ref([])
const viewDialogVisible = ref(false)
const currentAlbum = ref(null)
const currentPhotos = ref([])

// 图片预览
const showViewer = ref(false)
const currentPhotoIndex = ref(0)
const photoUrls = computed(() => currentPhotos.value.map(p => p.url))

// 获取相册列表
const fetchAlbums = async () => {
  try {
    const res = await getMyAlbums()
    albumList.value = res.data || []
  } catch (error) {
    ElMessage.error('获取相册列表失败')
  }
}

// 查看相册
const viewAlbum = async (album) => {
  currentAlbum.value = album
  try {
    const res = await getAlbumPhotos(album.id)
    currentPhotos.value = res.data || []
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取相册照片失败')
  }
}

// 关闭对话框
const closeDialog = () => {
  currentPhotos.value = []
  currentAlbum.value = null
}

// 预览照片
const previewPhoto = (index) => {
  currentPhotoIndex.value = index
  showViewer.value = true
}

// 下载照片
const downloadPhoto = async (photo) => {
  try {
    const link = document.createElement('a')
    link.href = photo.url
    link.download = `photo_${Date.now()}.jpg`
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('开始下载')
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 初始化
onMounted(() => {
  fetchAlbums()
})
</script>

<style scoped>
.albums-container {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
}

.album-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.album-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.album-cover {
  width: 100%;
  height: 250px;
  object-fit: cover;
  display: block;
}

.album-info {
  padding: 20px;
}

.album-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 12px;
  color: #333;
}

.album-meta {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.album-description {
  font-size: 14px;
  color: #999;
  margin-top: 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.album-header {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.album-info-detail {
  color: #666;
}

.album-desc {
  font-size: 14px;
  margin-bottom: 8px;
  line-height: 1.6;
}

.album-time {
  font-size: 13px;
  color: #999;
}

.photo-waterfall {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  padding: 10px;
}

.photo-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.photo-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.photo-image {
  width: 100%;
  height: 250px;
  display: block;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 14px;
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .photo-waterfall {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .album-cover {
    height: 180px;
  }

  .photo-image {
    height: 180px;
  }
}
</style>
