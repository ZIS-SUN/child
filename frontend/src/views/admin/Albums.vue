<template>
  <div class="albums-container">
    <h2>成长相册管理</h2>

    <el-card>
      <!-- 操作栏 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-select v-model="searchForm.classId" placeholder="选择班级" clearable @change="fetchAlbums">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="showCreateDialog">创建相册</el-button>
        </el-col>
      </el-row>

      <!-- 相册列表 -->
      <el-row :gutter="20">
        <el-col :span="6" v-for="album in albumList" :key="album.id">
          <el-card class="album-card" :body-style="{ padding: '0' }">
            <img
              :src="album.coverUrl || '/default-album.jpg'"
              class="album-cover"
              @click="viewAlbum(album)"
            />
            <div class="album-info">
              <div class="album-title">{{ album.title }}</div>
              <div class="album-meta">
                <span>{{ album.className }}</span>
                <span>{{ album.photoCount || 0 }}张照片</span>
              </div>
              <div class="album-actions">
                <el-button type="text" size="small" @click="editAlbum(album)">编辑</el-button>
                <el-button type="text" size="small" @click="addPhotos(album)">添加照片</el-button>
                <el-button type="text" size="small" @click="deleteAlbum(album)" style="color: #f56c6c">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="albumList.length === 0" description="暂无相册" />
    </el-card>

    <!-- 创建/编辑相册对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="albumForm" :rules="albumRules" ref="albumFormRef" label-width="100px">
        <el-form-item label="相册标题" prop="title">
          <el-input v-model="albumForm.title" placeholder="请输入相册标题" />
        </el-form-item>
        <el-form-item label="所属班级" prop="classId">
          <el-select v-model="albumForm.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="item in classList"
              :key="item.id"
              :label="item.className"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="相册描述" prop="description">
          <el-input
            v-model="albumForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入相册描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAlbum">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加照片对话框 -->
    <el-dialog
      v-model="photoDialogVisible"
      title="添加照片"
      width="600px"
      @close="resetPhotoForm"
    >
      <el-upload
        class="upload-demo"
        drag
        action="http://localhost:8080/api/upload/image"
        :headers="uploadHeaders"
        multiple
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :file-list="photoList"
        list-type="picture-card"
        accept="image/*"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 jpg/png/jpeg 格式图片,单个文件不超过5MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="photoDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPhotos">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看相册对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      :title="currentAlbum?.title"
      width="80%"
      top="5vh"
    >
      <div class="photo-grid">
        <div
          v-for="(photo, index) in currentPhotos"
          :key="index"
          class="photo-item"
        >
          <el-image
            :src="photo.url"
            :preview-src-list="currentPhotos.map(p => p.url)"
            :initial-index="index"
            fit="cover"
            class="photo-image"
          />
          <div class="photo-actions">
            <el-button
              type="danger"
              size="small"
              @click="deletePhoto(photo)"
            >删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-if="currentPhotos.length === 0" description="暂无照片" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import {
  getAlbumList,
  createAlbum,
  updateAlbum,
  deleteAlbum as deleteAlbumApi,
  addAlbumPhotos,
  deleteAlbumPhoto,
  getClassList
} from '@/api/admin'

// 数据
const classList = ref([])
const albumList = ref([])
const searchForm = reactive({
  classId: null
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('创建相册')
const albumFormRef = ref(null)
const albumForm = reactive({
  id: null,
  title: '',
  classId: null,
  description: ''
})

const albumRules = {
  title: [{ required: true, message: '请输入相册标题', trigger: 'blur' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

// 照片上传
const photoDialogVisible = ref(false)
const currentAlbumId = ref(null)
const photoList = ref([])
const uploadedPhotos = ref([])
const uploadHeaders = {
  'Authorization': localStorage.getItem('token')
}

// 查看相册
const viewDialogVisible = ref(false)
const currentAlbum = ref(null)
const currentPhotos = ref([])

// 获取班级列表
const fetchClasses = async () => {
  try {
    const res = await getClassList()
    classList.value = res.data || []
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  }
}

// 获取相册列表
const fetchAlbums = async () => {
  try {
    const params = {}
    if (searchForm.classId) {
      params.classId = searchForm.classId
    }
    const res = await getAlbumList(params)
    albumList.value = res.data || []
  } catch (error) {
    ElMessage.error('获取相册列表失败')
  }
}

// 显示创建对话框
const showCreateDialog = () => {
  dialogTitle.value = '创建相册'
  albumForm.id = null
  dialogVisible.value = true
}

// 编辑相册
const editAlbum = (album) => {
  dialogTitle.value = '编辑相册'
  albumForm.id = album.id
  albumForm.title = album.title
  albumForm.classId = album.classId
  albumForm.description = album.description
  dialogVisible.value = true
}

// 提交相册
const submitAlbum = async () => {
  await albumFormRef.value.validate()
  try {
    if (albumForm.id) {
      await updateAlbum(albumForm.id, albumForm)
      ElMessage.success('更新成功')
    } else {
      await createAlbum(albumForm)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchAlbums()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除相册
const deleteAlbum = async (album) => {
  try {
    await ElMessageBox.confirm('确定要删除该相册吗?', '提示', {
      type: 'warning'
    })
    await deleteAlbumApi(album.id)
    ElMessage.success('删除成功')
    fetchAlbums()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 添加照片
const addPhotos = (album) => {
  currentAlbumId.value = album.id
  photoDialogVisible.value = true
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 上传成功回调
const handleUploadSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    uploadedPhotos.value.push(response.data)
    ElMessage.success(`${file.name} 上传成功`)
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败回调
const handleUploadError = (error, file) => {
  console.error('Upload error:', error)
  ElMessage.error(`${file.name} 上传失败`)
}

// 提交照片
const submitPhotos = async () => {
  if (uploadedPhotos.value.length === 0) {
    ElMessage.warning('请先上传照片')
    return
  }
  try {
    await addAlbumPhotos(currentAlbumId.value, {
      photoUrls: uploadedPhotos.value
    })
    ElMessage.success('添加成功')
    photoDialogVisible.value = false
    fetchAlbums()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

// 查看相册
const viewAlbum = async (album) => {
  currentAlbum.value = album
  // 这里需要一个获取相册照片的接口
  currentPhotos.value = album.photos || []
  viewDialogVisible.value = true
}

// 删除照片
const deletePhoto = async (photo) => {
  try {
    await ElMessageBox.confirm('确定要删除该照片吗?', '提示', {
      type: 'warning'
    })
    await deleteAlbumPhoto(currentAlbum.value.id, { photoUrl: photo.url })
    ElMessage.success('删除成功')
    currentPhotos.value = currentPhotos.value.filter(p => p.url !== photo.url)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 重置表单
const resetForm = () => {
  albumForm.id = null
  albumForm.title = ''
  albumForm.classId = null
  albumForm.description = ''
  albumFormRef.value?.resetFields()
}

const resetPhotoForm = () => {
  photoList.value = []
  uploadedPhotos.value = []
}

// 初始化
onMounted(() => {
  fetchClasses()
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
}

.album-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.album-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.album-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.album-info {
  padding: 15px;
}

.album-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.album-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.album-actions {
  display: flex;
  justify-content: space-around;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  padding: 10px;
}

.photo-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 200px;
}

.photo-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  padding: 10px;
  text-align: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-item:hover .photo-actions {
  opacity: 1;
}
</style>
