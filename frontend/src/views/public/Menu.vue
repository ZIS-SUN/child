<template>
  <div class="menu-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">每周食谱</h1>
        <p class="page-subtitle">营养均衡 · 健康成长</p>
      </div>

      <!-- 周选择器 -->
      <div class="week-selector">
        <el-button :icon="ArrowLeft" @click="prevWeek" circle></el-button>
        <div class="week-info">
          <h3>{{ currentWeekText }}</h3>
          <p>{{ weekDateRange }}</p>
        </div>
        <el-button :icon="ArrowRight" @click="nextWeek" circle></el-button>
      </div>

      <!-- 食谱表格 -->
      <div class="menu-container" v-loading="loading">
        <div class="menu-table">
          <div class="menu-row menu-header-row">
            <div class="menu-cell day-cell">日期</div>
            <div class="menu-cell meal-cell">
              <span class="meal-icon">🌅</span>
              早餐
            </div>
            <div class="menu-cell meal-cell">
              <span class="meal-icon">🍎</span>
              加餐
            </div>
            <div class="menu-cell meal-cell">
              <span class="meal-icon">🍱</span>
              午餐
            </div>
            <div class="menu-cell meal-cell">
              <span class="meal-icon">🍰</span>
              点心
            </div>
          </div>

          <div class="menu-row" v-for="day in weeklyMenu" :key="day.day">
            <div class="menu-cell day-cell">
              <div class="day-name">{{ day.dayName }}</div>
              <div class="day-date">{{ day.date }}</div>
            </div>
            <div class="menu-cell meal-cell">
              <div class="meal-content">
                <p v-for="(item, index) in day.breakfast" :key="index">
                  {{ item.name }}
                  <el-tag v-if="item.allergens" type="warning" size="small" class="allergen-tag">
                    {{ item.allergens }}
                  </el-tag>
                </p>
              </div>
            </div>
            <div class="menu-cell meal-cell">
              <div class="meal-content">
                <p v-for="(item, index) in day.snack1" :key="index">
                  {{ item.name }}
                  <el-tag v-if="item.allergens" type="warning" size="small" class="allergen-tag">
                    {{ item.allergens }}
                  </el-tag>
                </p>
              </div>
            </div>
            <div class="menu-cell meal-cell">
              <div class="meal-content">
                <p v-for="(item, index) in day.lunch" :key="index">
                  {{ item.name }}
                  <el-tag v-if="item.allergens" type="warning" size="small" class="allergen-tag">
                    {{ item.allergens }}
                  </el-tag>
                </p>
              </div>
            </div>
            <div class="menu-cell meal-cell">
              <div class="meal-content">
                <p v-for="(item, index) in day.snack2" :key="index">
                  {{ item.name }}
                  <el-tag v-if="item.allergens" type="warning" size="small" class="allergen-tag">
                    {{ item.allergens }}
                  </el-tag>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 营养说明 -->
      <div class="nutrition-info">
        <el-card shadow="hover">
          <template #header>
            <div class="card-title">
              <span class="title-icon">💡</span>
              <span>营养与健康提示</span>
            </div>
          </template>
          <div class="info-content">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="info-item">
                  <h4>🥗 均衡膳食</h4>
                  <p>每餐搭配谷物、蔬菜、蛋白质，保证营养均衡</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="info-item">
                  <h4>🛡️ 过敏原标注</h4>
                  <p>含有花生、牛奶、鸡蛋、海鲜等过敏原的食物会特别标注</p>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="info-item">
                  <h4>👨‍🍳 专业配餐</h4>
                  <p>由专业营养师和厨师团队精心搭配</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </div>

      <!-- 过敏原说明 -->
      <div class="allergen-legend">
        <h3>常见过敏原标识</h3>
        <div class="legend-list">
          <el-tag type="warning" size="small">花生</el-tag>
          <el-tag type="warning" size="small">牛奶</el-tag>
          <el-tag type="warning" size="small">鸡蛋</el-tag>
          <el-tag type="warning" size="small">海鲜</el-tag>
          <el-tag type="warning" size="small">麸质</el-tag>
          <el-tag type="warning" size="small">坚果</el-tag>
        </div>
        <p class="legend-note">如您的孩子对某些食物过敏，请及时告知班级老师</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getWeeklyMenu } from '@/api/public'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const loading = ref(false)
const currentWeekOffset = ref(0)

const weeklyMenu = ref([
  {
    day: 1,
    dayName: '周一',
    date: '10/28',
    breakfast: [
      { name: '小米粥', allergens: '' },
      { name: '鸡蛋', allergens: '鸡蛋' },
      { name: '牛奶', allergens: '牛奶' },
      { name: '馒头', allergens: '' }
    ],
    snack1: [
      { name: '香蕉', allergens: '' },
      { name: '核桃', allergens: '坚果' }
    ],
    lunch: [
      { name: '番茄炒蛋', allergens: '鸡蛋' },
      { name: '清炒油菜', allergens: '' },
      { name: '米饭', allergens: '' },
      { name: '紫菜蛋花汤', allergens: '鸡蛋、海鲜' }
    ],
    snack2: [
      { name: '酸奶', allergens: '牛奶' },
      { name: '饼干', allergens: '麸质' }
    ]
  },
  {
    day: 2,
    dayName: '周二',
    date: '10/29',
    breakfast: [
      { name: '豆浆', allergens: '' },
      { name: '包子', allergens: '麸质' },
      { name: '水果', allergens: '' }
    ],
    snack1: [
      { name: '苹果', allergens: '' }
    ],
    lunch: [
      { name: '红烧肉', allergens: '' },
      { name: '炒青菜', allergens: '' },
      { name: '米饭', allergens: '' },
      { name: '冬瓜汤', allergens: '' }
    ],
    snack2: [
      { name: '蛋糕', allergens: '鸡蛋、牛奶、麸质' }
    ]
  },
  {
    day: 3,
    dayName: '周三',
    date: '10/30',
    breakfast: [
      { name: '牛奶', allergens: '牛奶' },
      { name: '面包', allergens: '麸质' },
      { name: '鸡蛋', allergens: '鸡蛋' }
    ],
    snack1: [
      { name: '橙子', allergens: '' }
    ],
    lunch: [
      { name: '糖醋排骨', allergens: '' },
      { name: '炒西兰花', allergens: '' },
      { name: '米饭', allergens: '' },
      { name: '青菜汤', allergens: '' }
    ],
    snack2: [
      { name: '牛奶布丁', allergens: '牛奶' }
    ]
  },
  {
    day: 4,
    dayName: '周四',
    date: '10/31',
    breakfast: [
      { name: '南瓜粥', allergens: '' },
      { name: '包子', allergens: '麸质' },
      { name: '鸡蛋', allergens: '鸡蛋' }
    ],
    snack1: [
      { name: '梨', allergens: '' }
    ],
    lunch: [
      { name: '清蒸鱼', allergens: '海鲜' },
      { name: '炒菠菜', allergens: '' },
      { name: '米饭', allergens: '' },
      { name: '豆腐汤', allergens: '' }
    ],
    snack2: [
      { name: '面包', allergens: '麸质' },
      { name: '牛奶', allergens: '牛奶' }
    ]
  },
  {
    day: 5,
    dayName: '周五',
    date: '11/01',
    breakfast: [
      { name: '八宝粥', allergens: '' },
      { name: '鸡蛋', allergens: '鸡蛋' },
      { name: '馒头', allergens: '麸质' }
    ],
    snack1: [
      { name: '葡萄', allergens: '' }
    ],
    lunch: [
      { name: '宫保鸡丁', allergens: '花生' },
      { name: '炒青菜', allergens: '' },
      { name: '米饭', allergens: '' },
      { name: '萝卜汤', allergens: '' }
    ],
    snack2: [
      { name: '水果沙拉', allergens: '' }
    ]
  }
])

const currentWeekText = computed(() => {
  const weekNum = Math.abs(currentWeekOffset.value) + 1
  if (currentWeekOffset.value === 0) {
    return '本周食谱'
  } else if (currentWeekOffset.value > 0) {
    return `下${weekNum}周食谱`
  } else {
    return `上${weekNum}周食谱`
  }
})

const weekDateRange = computed(() => {
  const today = new Date()
  const offset = currentWeekOffset.value * 7
  const startDate = new Date(today.getTime() + offset * 24 * 60 * 60 * 1000)
  startDate.setDate(startDate.getDate() - startDate.getDay() + 1)
  const endDate = new Date(startDate.getTime() + 4 * 24 * 60 * 60 * 1000)

  const formatDate = (date) => {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }

  return `${formatDate(startDate)} - ${formatDate(endDate)}`
})

const fetchWeeklyMenu = async () => {
  loading.value = true
  try {
    const res = await getWeeklyMenu({ weekOffset: currentWeekOffset.value })
    weeklyMenu.value = res.data.map(item => ({
      weekday: item.weekday,
      breakfast: typeof item.breakfast === 'string' ? JSON.parse(item.breakfast) : item.breakfast,
      snack1: typeof item.snack1 === 'string' ? JSON.parse(item.snack1) : item.snack1,
      lunch: typeof item.lunch === 'string' ? JSON.parse(item.lunch) : item.lunch,
      snack2: typeof item.snack2 === 'string' ? JSON.parse(item.snack2) : item.snack2
    }))
  } catch (error) {
    console.error('获取食谱失败', error)
    ElMessage.error('获取食谱失败')
  } finally {
    loading.value = false
  }
}

const prevWeek = () => {
  currentWeekOffset.value--
  fetchWeeklyMenu()
}

const nextWeek = () => {
  currentWeekOffset.value++
  fetchWeeklyMenu()
}

onMounted(() => {
  fetchWeeklyMenu()
})
</script>

<style scoped>
.menu-page {
  min-height: 100vh;
  padding: 40px 0;
  background: linear-gradient(to bottom, #f8f9fe 0%, #ffffff 100%);
}

.container {
  max-width: 1400px;
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

.week-selector {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 32px;
  margin-bottom: 32px;
}

.week-info {
  text-align: center;
  min-width: 200px;
}

.week-info h3 {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.week-info p {
  font-size: 14px;
  color: #7f8c8d;
}

.menu-container {
  margin-bottom: 40px;
}

.menu-table {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.menu-row {
  display: grid;
  grid-template-columns: 120px repeat(4, 1fr);
  border-bottom: 1px solid #eee;
}

.menu-row:last-child {
  border-bottom: none;
}

.menu-header-row {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.menu-header-row .menu-cell {
  font-weight: 600;
  font-size: 16px;
}

.menu-cell {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-right: 1px solid #eee;
}

.menu-cell:last-child {
  border-right: none;
}

.day-cell {
  background: #f8f9fe;
  flex-direction: column;
  gap: 4px;
}

.day-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.day-date {
  font-size: 14px;
  color: #7f8c8d;
}

.meal-cell {
  flex-direction: column;
  align-items: flex-start;
}

.meal-icon {
  font-size: 20px;
  margin-right: 8px;
}

.meal-content {
  width: 100%;
}

.meal-content p {
  margin-bottom: 8px;
  color: #2c3e50;
  line-height: 1.6;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.meal-content p:last-child {
  margin-bottom: 0;
}

.allergen-tag {
  font-size: 11px;
  padding: 0 6px;
  height: 20px;
  line-height: 20px;
}

.nutrition-info {
  margin-bottom: 32px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.title-icon {
  font-size: 24px;
}

.info-content {
  padding: 16px 0;
}

.info-item {
  text-align: center;
  padding: 20px;
}

.info-item h4 {
  font-size: 16px;
  color: #2c3e50;
  margin-bottom: 12px;
}

.info-item p {
  font-size: 14px;
  color: #7f8c8d;
  line-height: 1.6;
}

.allergen-legend {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.allergen-legend h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 16px;
}

.legend-list {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.legend-note {
  font-size: 14px;
  color: #7f8c8d;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }

  .menu-table {
    overflow-x: auto;
  }

  .menu-row {
    min-width: 800px;
  }

  .info-content .el-col {
    margin-bottom: 16px;
  }
}
</style>
