<template>
  <div class="teachers-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">师资风采</h1>
        <p class="page-subtitle">专业团队 · 用心育人</p>
      </div>

      <!-- 班级筛选 -->
      <div class="class-filter">
        <el-radio-group v-model="selectedClass" size="large" @change="handleClassChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="admin">园领导</el-radio-button>
          <el-radio-button label="senior">大班</el-radio-button>
          <el-radio-button label="middle">中班</el-radio-button>
          <el-radio-button label="junior">小班</el-radio-button>
          <el-radio-button label="special">特色课程</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 教师列表 -->
      <div class="teachers-container" v-loading="loading">
        <div class="teachers-grid">
          <div class="teacher-card" v-for="teacher in filteredTeachers" :key="teacher.id" @click="showTeacherDetail(teacher)">
            <div class="teacher-image">
              <img :src="teacher.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(teacher.name)}&background=667eea&color=fff&size=300`" :alt="teacher.name">
              <div class="click-hint">
                <el-icon><View /></el-icon>
                <span>查看详情</span>
              </div>
            </div>
            <div class="teacher-info">
              <h3 class="teacher-name">{{ teacher.name }}</h3>
              <p class="teacher-position">{{ teacher.position }}</p>
              <p class="teacher-class" v-if="teacher.className">{{ teacher.className }}</p>
              <div class="teacher-tags">
                <el-tag v-for="tag in teacher.tags" :key="tag" size="small" type="success" effect="plain">
                  {{ tag }}
                </el-tag>
              </div>
              <div class="teacher-description">
                {{ teacher.description }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 教师详情对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="selectedTeacher?.name"
        width="600px"
        center
      >
        <div class="teacher-detail" v-if="selectedTeacher">
          <div class="detail-avatar">
            <img :src="selectedTeacher.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(selectedTeacher.name)}&background=667eea&color=fff&size=200`" :alt="selectedTeacher.name">
          </div>
          <div class="detail-info">
            <h2>{{ selectedTeacher.name }}</h2>
            <p class="detail-position">{{ selectedTeacher.position }}</p>
            <p class="detail-class" v-if="selectedTeacher.className">所属班级：{{ selectedTeacher.className }}</p>

            <div class="detail-section">
              <h3>专业资质</h3>
              <div class="detail-tags">
                <el-tag v-for="tag in selectedTeacher.tags" :key="tag" size="large" type="success" effect="light">
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <div class="detail-section">
              <h3>个人简介</h3>
              <p class="detail-description">{{ selectedTeacher.description }}</p>
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getTeacherList } from '@/api/public'
import { ElMessage } from 'element-plus'
import { View } from '@element-plus/icons-vue'

const loading = ref(false)
const selectedClass = ref('all')
const dialogVisible = ref(false)
const selectedTeacher = ref(null)

const teacherList = ref([
  {
    id: 1,
    name: '张慧敏',
    position: '园长 / 高级教师',
    className: '',
    classType: 'admin',
    avatar: '',
    tags: ['20年教龄', '特级教师', '学前教育专家'],
    description: '从事幼教工作20余年，具有丰富的教学和管理经验，多次获得市级优秀教师称号。'
  },
  {
    id: 2,
    name: '李婷婷',
    position: '副园长 / 教研组长',
    className: '',
    classType: 'admin',
    avatar: '',
    tags: ['15年教龄', '教研专家', '课程开发'],
    description: '专注于幼儿园课程研发与教师培训，主导多个省级教研课题。'
  },
  {
    id: 3,
    name: '王芳',
    position: '大一班班主任',
    className: '大一班',
    classType: 'senior',
    avatar: '',
    tags: ['10年教龄', '蒙氏认证', '优秀班主任'],
    description: '热爱幼教事业，善于发现每个孩子的闪光点，培养孩子的独立性和创造力。'
  },
  {
    id: 4,
    name: '刘梅',
    position: '大二班班主任',
    className: '大二班',
    classType: 'senior',
    avatar: '',
    tags: ['12年教龄', '心理咨询师', '艺术教育'],
    description: '注重儿童心理健康教育，擅长通过艺术活动培养孩子的表达能力。'
  },
  {
    id: 5,
    name: '陈静',
    position: '中一班班主任',
    className: '中一班',
    classType: 'middle',
    avatar: '',
    tags: ['8年教龄', '音乐教育', '游戏教学'],
    description: '善于通过音乐和游戏引导孩子学习，课堂氛围活跃，深受孩子喜爱。'
  },
  {
    id: 6,
    name: '赵敏',
    position: '中二班班主任',
    className: '中二班',
    classType: 'middle',
    avatar: '',
    tags: ['9年教龄', '科学启蒙', 'STEAM教育'],
    description: '致力于儿童科学启蒙教育，培养孩子的探索精神和动手能力。'
  },
  {
    id: 7,
    name: '杨丽',
    position: '小一班班主任',
    className: '小一班',
    classType: 'junior',
    avatar: '',
    tags: ['7年教龄', '保育专家', '情绪管理'],
    description: '耐心细致，善于帮助小班幼儿适应幼儿园生活，建立安全感。'
  },
  {
    id: 8,
    name: '周婷',
    position: '小二班班主任',
    className: '小二班',
    classType: 'junior',
    avatar: '',
    tags: ['6年教龄', '绘本阅读', '语言发展'],
    description: '擅长通过绘本阅读培养孩子的语言表达和想象力。'
  },
  {
    id: 9,
    name: '吴老师',
    position: '美术教师',
    className: '',
    classType: 'special',
    avatar: '',
    tags: ['艺术教育', '美术专业', '创意课程'],
    description: '毕业于美术学院，专注儿童创意美术教育，激发孩子的艺术天赋。'
  },
  {
    id: 10,
    name: '郑老师',
    position: '体育教师',
    className: '',
    classType: 'special',
    avatar: '',
    tags: ['体育教育', '运动训练', '趣味体能'],
    description: '通过趣味体育活动，培养孩子的运动兴趣和团队协作能力。'
  },
  {
    id: 11,
    name: '孙老师',
    position: '英语教师',
    className: '',
    classType: 'special',
    avatar: '',
    tags: ['英语教育', 'TKT认证', '互动教学'],
    description: '擅长通过游戏和互动，让孩子在轻松愉快的氛围中学习英语。'
  },
  {
    id: 12,
    name: '林老师',
    position: '音乐教师',
    className: '',
    classType: 'special',
    avatar: '',
    tags: ['音乐教育', '钢琴十级', '奥尔夫教学'],
    description: '运用奥尔夫音乐教学法，培养孩子的音乐素养和节奏感。'
  }
])

const filteredTeachers = computed(() => {
  if (selectedClass.value === 'all') {
    return teacherList.value
  }
  return teacherList.value.filter(t => t.classType === selectedClass.value)
})

const fetchTeachers = async () => {
  loading.value = true
  try {
    // 总是获取所有教师，然后通过computed进行前端过滤
    const res = await getTeacherList('all')
    // 处理tags字段，将JSON字符串转换为数组
    teacherList.value = res.data.map(teacher => ({
      ...teacher,
      tags: typeof teacher.tags === 'string' ? JSON.parse(teacher.tags) : teacher.tags
    }))
  } catch (error) {
    console.error('获取教师列表失败', error)
    ElMessage.error('获取教师列表失败')
  } finally {
    loading.value = false
  }
}

const handleClassChange = () => {
  // 可以添加动画效果
}

const showTeacherDetail = (teacher) => {
  selectedTeacher.value = teacher
  dialogVisible.value = true
}

onMounted(() => {
  fetchTeachers()
})
</script>

<style scoped>
.teachers-page {
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

.class-filter {
  display: flex;
  justify-content: center;
  margin-bottom: 40px;
}

.class-filter :deep(.el-radio-button__inner) {
  padding: 12px 24px;
  font-weight: 500;
}

.teachers-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.teacher-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  cursor: pointer;
}

.teacher-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.teacher-card:hover .click-hint {
  opacity: 1;
}

.teacher-image {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.click-hint {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(102, 126, 234, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: white;
  font-size: 16px;
  font-weight: 500;
  opacity: 0;
  transition: opacity 0.3s;
}

.click-hint .el-icon {
  font-size: 32px;
}

.teacher-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.teacher-card:hover .teacher-image img {
  transform: scale(1.1);
}

.teacher-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(102, 126, 234, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.teacher-card:hover .teacher-overlay {
  opacity: 1;
}

.teacher-info {
  padding: 24px;
}

.teacher-name {
  font-size: 22px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.teacher-position {
  font-size: 15px;
  color: #667eea;
  font-weight: 500;
  margin-bottom: 4px;
}

.teacher-class {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 12px;
}

.teacher-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.teacher-description {
  font-size: 14px;
  color: #5a6c7d;
  line-height: 1.8;
}

@media (max-width: 1024px) {
  .teachers-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }

  .class-filter :deep(.el-radio-button__inner) {
    padding: 8px 16px;
    font-size: 14px;
  }

  .teachers-grid {
    grid-template-columns: 1fr;
  }
}

/* 详情对话框样式 */
.teacher-detail {
  text-align: center;
}

.detail-avatar {
  margin-bottom: 24px;
}

.detail-avatar img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.detail-info h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.detail-position {
  font-size: 18px;
  color: #667eea;
  margin-bottom: 16px;
}

.detail-class {
  font-size: 16px;
  color: #666;
  margin-bottom: 24px;
}

.detail-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  text-align: left;
}

.detail-section h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-section h3::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.detail-description {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
  text-align: justify;
}
</style>
