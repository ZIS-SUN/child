<template>
  <div class="notice-detail-page">
    <div class="container">
      <el-button :icon="ArrowLeft" @click="$router.back()" class="back-button">返回列表</el-button>

      <div class="notice-detail" v-loading="loading">
        <div class="notice-header">
          <h1 class="notice-title">{{ notice.title }}</h1>
          <div class="notice-meta">
            <span class="meta-item">
              <el-icon><Clock /></el-icon>
              发布时间：{{ notice.publishTime }}
            </span>
            <span class="meta-item">
              <el-icon><View /></el-icon>
              浏览次数：{{ notice.views || 0 }}
            </span>
            <span class="meta-item" v-if="notice.author">
              <el-icon><User /></el-icon>
              发布人：{{ notice.author }}
            </span>
          </div>
        </div>

        <el-divider />

        <div class="notice-content" v-html="notice.content"></div>

        <div class="notice-attachments" v-if="notice.attachments && notice.attachments.length > 0">
          <h3>相关附件</h3>
          <div class="attachment-list">
            <div class="attachment-item" v-for="(file, index) in notice.attachments" :key="index">
              <el-icon><Document /></el-icon>
              <span>{{ file.name }}</span>
              <el-button type="primary" link>下载</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 相关公告 -->
      <div class="related-notices" v-if="relatedNotices.length > 0">
        <h2 class="section-title">相关公告</h2>
        <div class="related-list">
          <div
            class="related-item"
            v-for="item in relatedNotices"
            :key="item.id"
            @click="handleNoticeClick(item.id)"
          >
            <h3>{{ item.title }}</h3>
            <span class="date">{{ formatDate(item.publishTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNoticeDetail } from '@/api/public'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Clock, View, User, Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)

const notice = ref({
  id: null,
  title: '',
  content: '',
  publishTime: '',
  author: '',
  views: 0,
  attachments: []
})

const relatedNotices = ref([
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

const fetchNoticeDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    // 调用真实API
    // const res = await getNoticeDetail(id)
    // notice.value = res.data

    // 模拟数据
    notice.value = {
      id: id,
      title: '关于2024年秋季招生工作的通知',
      content: `
        <h2>各位家长：</h2>
        <p>为做好2024年秋季招生工作，现将有关事项通知如下：</p>

        <h3>一、招生对象</h3>
        <p>2020年9月1日至2021年8月31日期间出生的适龄儿童。</p>

        <h3>二、招生计划</h3>
        <p>小班：4个班，每班25人，共100人。</p>

        <h3>三、报名时间</h3>
        <p>2024年6月1日至6月30日，每天上午9:00-11:30，下午2:00-5:00。</p>

        <h3>四、报名方式</h3>
        <p>1. 网上报名：登录幼儿园官网进入"在线报名"系统填写信息。</p>
        <p>2. 现场报名：携带相关材料到幼儿园招生办公室报名。</p>

        <h3>五、所需材料</h3>
        <ul>
          <li>幼儿户口簿原件及复印件</li>
          <li>幼儿出生证明原件及复印件</li>
          <li>幼儿预防接种证原件及复印件</li>
          <li>家长身份证原件及复印件</li>
          <li>近期免冠照片2张</li>
        </ul>

        <h3>六、录取方式</h3>
        <p>根据报名顺序和幼儿年龄，择优录取。录取结果将于7月15日前以短信和电话方式通知家长。</p>

        <h3>七、联系方式</h3>
        <p>咨询电话：0123-12345678</p>
        <p>联系人：李老师</p>

        <p style="margin-top: 30px; text-align: right;">UltraThink 幼儿园</p>
        <p style="text-align: right;">2024年10月20日</p>
      `,
      publishTime: '2024-10-20 10:00:00',
      author: '教务处',
      views: 156,
      attachments: [
        { name: '招生简章.pdf', url: '' },
        { name: '报名表.doc', url: '' }
      ]
    }
  } catch (error) {
    ElMessage.error('获取公告详情失败')
  } finally {
    loading.value = false
  }
}

const handleNoticeClick = (id) => {
  router.push(`/public/notices/${id}`)
  fetchNoticeDetail()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split(' ')[0]
}

onMounted(() => {
  fetchNoticeDetail()
})
</script>

<style scoped>
.notice-detail-page {
  min-height: 100vh;
  padding: 40px 0;
  background: #f8f9fe;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-button {
  margin-bottom: 24px;
}

.notice-detail {
  background: white;
  border-radius: 16px;
  padding: 48px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 32px;
}

.notice-header {
  margin-bottom: 24px;
}

.notice-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1.4;
  margin-bottom: 16px;
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
  font-size: 16px;
  line-height: 1.8;
  color: #2c3e50;
}

.notice-content :deep(h2) {
  font-size: 24px;
  margin: 24px 0 16px;
  color: #2c3e50;
}

.notice-content :deep(h3) {
  font-size: 20px;
  margin: 20px 0 12px;
  color: #2c3e50;
}

.notice-content :deep(p) {
  margin-bottom: 12px;
}

.notice-content :deep(ul) {
  margin: 12px 0;
  padding-left: 24px;
}

.notice-content :deep(li) {
  margin-bottom: 8px;
}

.notice-attachments {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.notice-attachments h3 {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 16px;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8f9fe;
  border-radius: 8px;
  transition: all 0.3s;
}

.attachment-item:hover {
  background: #e8eaf6;
}

.attachment-item .el-icon {
  font-size: 20px;
  color: #667eea;
}

.attachment-item span {
  flex: 1;
  color: #2c3e50;
}

.related-notices {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.related-item:hover {
  background: #f8f9fe;
}

.related-item h3 {
  font-size: 16px;
  font-weight: 500;
  color: #2c3e50;
  flex: 1;
}

.related-item .date {
  font-size: 14px;
  color: #95a5a6;
}

@media (max-width: 768px) {
  .notice-detail {
    padding: 24px;
  }

  .notice-title {
    font-size: 24px;
  }

  .notice-content {
    font-size: 15px;
  }

  .related-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
