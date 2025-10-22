<template>
  <div class="public-home">
    <!-- 轮播图 -->
    <section class="hero-section">
      <el-carousel height="500px" :interval="5000">
        <el-carousel-item v-for="(image, index) in bannerImages" :key="index">
          <div class="carousel-item" :style="{ backgroundImage: `url(${image.url})` }">
            <div class="carousel-overlay">
              <h1 class="carousel-title">{{ image.title }}</h1>
              <p class="carousel-subtitle">{{ image.subtitle }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <div class="container">
      <!-- 园所简介 -->
      <section class="intro-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🏫</span>
            园所简介
          </h2>
        </div>
        <div class="intro-content">
          <div class="intro-text">
            <h3>欢迎来到 UltraThink 幼儿园</h3>
            <p>
              UltraThink幼儿园创办于2010年，是一所集教育、保育、营养为一体的现代化幼儿园。
              我们秉承"用心守护每一个孩子的成长"的教育理念，致力于为3-6岁儿童提供优质的学前教育服务。
            </p>
            <p>
              园所占地面积3000平方米，拥有宽敞明亮的教室、专业的活动室、安全的户外活动场地。
              我们采用蒙台梭利教育理念，注重培养孩子的独立性、创造力和社交能力。
            </p>
            <div class="intro-stats">
              <div class="stat-item">
                <div class="stat-value">14</div>
                <div class="stat-label">年办园历史</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">300+</div>
                <div class="stat-label">在园幼儿</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">50+</div>
                <div class="stat-label">专业教师</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">98%</div>
                <div class="stat-label">家长满意度</div>
              </div>
            </div>
          </div>
          <div class="intro-image">
            <img src="https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=500&h=400&fit=crop" alt="园所环境">
          </div>
        </div>
      </section>

      <!-- 师资力量卡片 -->
      <section class="teachers-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">👨‍🏫</span>
            优秀师资
          </h2>
          <el-button type="primary" link @click="$router.push('/public/teachers')">
            查看全部 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="teachers-grid" v-loading="loading">
          <div class="teacher-card" v-for="teacher in teachers" :key="teacher.id">
            <div class="teacher-avatar">
              <img :src="teacher.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(teacher.name)}&background=667eea&color=fff&size=120`" :alt="teacher.name">
            </div>
            <div class="teacher-info">
              <h3 class="teacher-name">{{ teacher.name }}</h3>
              <p class="teacher-position">{{ teacher.position }}</p>
              <div class="teacher-tags">
                <el-tag v-for="tag in teacher.tags" :key="tag" size="small" type="success">{{ tag }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 当月食谱预览 -->
      <section class="menu-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">🍱</span>
            本周食谱
          </h2>
          <el-button type="primary" link @click="$router.push('/public/menu')">
            查看完整食谱 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="menu-preview">
          <el-card v-for="day in weeklyMenu.slice(0, 3)" :key="day.day" class="menu-card" shadow="hover">
            <template #header>
              <div class="menu-day">{{ day.dayName }}</div>
            </template>
            <div class="menu-items">
              <div class="menu-item">
                <span class="meal-type">早餐</span>
                <span class="meal-content">{{ day.breakfast }}</span>
              </div>
              <div class="menu-item">
                <span class="meal-type">午餐</span>
                <span class="meal-content">{{ day.lunch }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </section>

      <!-- 最新公告 -->
      <section class="notices-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">📢</span>
            最新公告
          </h2>
          <el-button type="primary" link @click="$router.push('/public/notices')">
            查看全部 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="notices-list">
          <div
            class="notice-item"
            v-for="notice in notices"
            :key="notice.id"
            @click="handleNoticeClick(notice.id)"
          >
            <div class="notice-badge" v-if="isNew(notice.publishTime)">NEW</div>
            <div class="notice-content">
              <h3 class="notice-title">{{ notice.title }}</h3>
              <p class="notice-date">
                <el-icon><Clock /></el-icon>
                {{ formatDate(notice.publishTime) }}
              </p>
            </div>
            <el-icon class="notice-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </section>

      <!-- 在线报名入口 -->
      <section class="enrollment-section">
        <div class="enrollment-banner">
          <div class="enrollment-content">
            <div class="enrollment-text">
              <h2>开启孩子的快乐成长之旅</h2>
              <p>名额有限，欢迎预约参观和在线报名</p>
            </div>
            <el-button type="primary" size="large" @click="$router.push('/public/enrollment')">
              立即报名 <el-icon class="el-icon--right"><Right /></el-icon>
            </el-button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHomePageData } from '@/api/public'
import { ElMessage } from 'element-plus'
import { ArrowRight, Clock, Right } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)

const bannerImages = ref([
  {
    url: 'https://images.unsplash.com/photo-1503454537195-1dcabb73ffb9?w=1200&h=500&fit=crop',
    title: '用心守护每一个孩子的成长',
    subtitle: '专业、温馨、有爱的学前教育'
  },
  {
    url: 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=1200&h=500&fit=crop',
    title: '现代化的教学设施',
    subtitle: '为孩子提供最好的成长空间'
  },
  {
    url: 'https://images.unsplash.com/photo-1560421683-6856ea585c78?w=1200&h=500&fit=crop',
    title: '寓教于乐的教学方式',
    subtitle: '在游戏中学习，在快乐中成长'
  }
])

const teachers = ref([
  {
    id: 1,
    name: '张老师',
    position: '园长 / 高级教师',
    avatar: '',
    tags: ['20年教龄', '特级教师']
  },
  {
    id: 2,
    name: '李老师',
    position: '大班班主任',
    avatar: '',
    tags: ['幼教专家', '蒙氏认证']
  },
  {
    id: 3,
    name: '王老师',
    position: '中班班主任',
    avatar: '',
    tags: ['音乐教育', '15年教龄']
  },
  {
    id: 4,
    name: '刘老师',
    position: '小班班主任',
    avatar: '',
    tags: ['心理咨询师', '艺术教育']
  }
])

const weeklyMenu = ref([
  { day: 1, dayName: '周一', breakfast: '小米粥、鸡蛋、牛奶', lunch: '番茄炒蛋、青菜、米饭' },
  { day: 2, dayName: '周二', breakfast: '豆浆、馒头、水果', lunch: '红烧肉、炒青菜、米饭' },
  { day: 3, dayName: '周三', breakfast: '牛奶、面包、鸡蛋', lunch: '糖醋排骨、青菜汤、米饭' },
  { day: 4, dayName: '周四', breakfast: '南瓜粥、包子', lunch: '清蒸鱼、蔬菜、米饭' },
  { day: 5, dayName: '周五', breakfast: '八宝粥、鸡蛋', lunch: '宫保鸡丁、青菜、米饭' }
])

const notices = ref([
  {
    id: 1,
    title: '关于2024年秋季招生工作的通知',
    publishTime: '2024-10-20 10:00:00'
  },
  {
    id: 2,
    title: '本周五举办家长开放日活动',
    publishTime: '2024-10-21 14:30:00'
  },
  {
    id: 3,
    title: '国庆节放假通知及注意事项',
    publishTime: '2024-10-18 09:00:00'
  }
])

const fetchHomeData = async () => {
  loading.value = true
  try {
    const res = await getHomePageData()
    // 处理教师数据，解析tags JSON字符串
    if (res.data.teachers) {
      teachers.value = res.data.teachers.map(teacher => ({
        ...teacher,
        tags: typeof teacher.tags === 'string' ? JSON.parse(teacher.tags) : teacher.tags
      }))
    }
    if (res.data.notices) {
      notices.value = res.data.notices
    }
    if (res.data.menu) {
      weeklyMenu.value = res.data.menu.map(item => ({
        day: item.weekday,
        dayName: ['周一', '周二', '周三', '周四', '周五'][item.weekday - 1],
        breakfast: JSON.parse(item.breakfast || '[]').map(i => i.name).join('、'),
        lunch: JSON.parse(item.lunch || '[]').map(i => i.name).join('、')
      }))
    }
  } catch (error) {
    console.error('获取首页数据失败', error)
    ElMessage.error('获取首页数据失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split(' ')[0]
}

const isNew = (dateStr) => {
  if (!dateStr) return false
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  return diff < 3 * 24 * 60 * 60 * 1000 // 3天内
}

const handleNoticeClick = (id) => {
  router.push(`/public/notices/${id}`)
}

onMounted(() => {
  fetchHomeData()
})
</script>

<style scoped>
.public-home {
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Hero Section */
.hero-section {
  margin-bottom: 60px;
}

.carousel-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
}

.carousel-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.carousel-subtitle {
  font-size: 20px;
  opacity: 0.95;
}

/* Section Common */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
}

.title-icon {
  font-size: 36px;
}

/* Intro Section */
.intro-section {
  margin-bottom: 80px;
}

.intro-content {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 60px;
  align-items: center;
}

.intro-text h3 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 20px;
}

.intro-text p {
  line-height: 1.8;
  color: #5a6c7d;
  margin-bottom: 16px;
  font-size: 16px;
}

.intro-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-top: 40px;
}

.stat-item {
  text-align: center;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.intro-image {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.intro-image img {
  width: 100%;
  height: auto;
  display: block;
}

/* Teachers Section */
.teachers-section {
  margin-bottom: 80px;
}

.teachers-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.teacher-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  cursor: pointer;
}

.teacher-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.teacher-avatar {
  width: 120px;
  height: 120px;
  margin: 0 auto 16px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #f0f2f5;
}

.teacher-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.teacher-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.teacher-position {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 12px;
}

.teacher-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
}

/* Menu Section */
.menu-section {
  margin-bottom: 80px;
}

.menu-preview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.menu-card {
  border-radius: 16px;
  overflow: hidden;
}

.menu-day {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.menu-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-item {
  display: flex;
  gap: 12px;
}

.meal-type {
  font-weight: 600;
  color: #667eea;
  min-width: 50px;
}

.meal-content {
  color: #5a6c7d;
  flex: 1;
}

/* Notices Section */
.notices-section {
  margin-bottom: 80px;
}

.notices-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.notice-item {
  background: white;
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.notice-item:hover {
  transform: translateX(8px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.notice-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.notice-content {
  flex: 1;
}

.notice-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.notice-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #95a5a6;
}

.notice-arrow {
  color: #bdc3c7;
  font-size: 18px;
  transition: all 0.3s;
}

.notice-item:hover .notice-arrow {
  color: #667eea;
  transform: translateX(4px);
}

/* Enrollment Section */
.enrollment-section {
  margin-bottom: 60px;
}

.enrollment-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  padding: 60px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.enrollment-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.enrollment-text h2 {
  font-size: 36px;
  margin-bottom: 12px;
}

.enrollment-text p {
  font-size: 18px;
  opacity: 0.95;
}

.enrollment-content .el-button {
  padding: 20px 40px;
  font-size: 18px;
  font-weight: 600;
}

/* Responsive */
@media (max-width: 768px) {
  .carousel-title {
    font-size: 28px;
  }

  .carousel-subtitle {
    font-size: 16px;
  }

  .intro-content {
    grid-template-columns: 1fr;
  }

  .intro-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .teachers-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .menu-preview {
    grid-template-columns: 1fr;
  }

  .enrollment-content {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
}
</style>
