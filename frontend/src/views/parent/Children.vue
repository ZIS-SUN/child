<template>
  <div class="children-container">
    <h2>我的孩子</h2>
    <el-row :gutter="20">
      <el-col :span="8" v-for="child in children" :key="child.id">
        <el-card shadow="hover" class="child-card">
          <div class="child-info">
            <el-avatar :size="80" :src="child.avatarUrl || '/default-avatar.png'" />
            <div class="child-details">
              <h3>{{ child.name }}</h3>
              <p>性别: {{ child.gender === 'M' ? '男' : '女' }}</p>
              <p>班级: {{ child.className || '未分配' }}</p>
              <p>班主任: {{ child.teacherName || '暂无' }}</p>
              <el-tag v-if="child.isPrimary" type="success" size="small">主显示</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="children.length === 0" description="暂无孩子信息" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyChildren } from '@/api/parent'
import { ElMessage } from 'element-plus'

const children = ref([])

const fetchChildren = async () => {
  try {
    const res = await getMyChildren()
    children.value = res.data || []
  } catch (error) {
    ElMessage.error('获取孩子信息失败')
  }
}

onMounted(() => {
  fetchChildren()
})
</script>

<style scoped>
.children-container {
  padding: 20px;
}

.child-card {
  margin-bottom: 20px;
}

.child-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.child-details {
  flex: 1;
}

.child-details h3 {
  margin: 0 0 10px 0;
}

.child-details p {
  margin: 5px 0;
  color: #666;
}
</style>
