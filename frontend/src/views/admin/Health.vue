<template>
  <div class="health-container">
    <h2>健康管理</h2>

    <el-card>
      <!-- 幼儿选择 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="8">
          <el-select
            v-model="selectedChildId"
            placeholder="选择幼儿"
            filterable
            @change="loadHealthData"
            style="width: 100%"
          >
            <el-option
              v-for="child in childrenList"
              :key="child.id"
              :label="`${child.name} - ${child.className}`"
              :value="child.id"
            />
          </el-select>
        </el-col>
      </el-row>

      <div v-if="selectedChildId">
        <!-- 基本健康信息 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>基本健康信息</span>
              <el-button type="primary" size="small" @click="showEditHealthDialog">编辑</el-button>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="血型">{{ healthInfo.bloodType || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="过敏史">{{ healthInfo.allergyInfo || '无' }}</el-descriptions-item>
            <el-descriptions-item label="病史" :span="2">{{ healthInfo.medicalHistory || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 体温记录 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>体温记录</span>
              <el-button type="success" size="small" @click="showAddTempDialog">添加体温</el-button>
            </div>
          </template>
          <el-table :data="temperatureList" style="width: 100%">
            <el-table-column prop="measureTime" label="测量时间" width="180" />
            <el-table-column prop="temperature" label="体温(°C)" width="120">
              <template #default="{ row }">
                <el-tag :type="getTempType(row.temperature)">{{ row.temperature }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="period" label="时段" width="100" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="deleteTempRecord(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="temperatureList.length === 0" description="暂无体温记录" />
        </el-card>

        <!-- 成长记录 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>成长记录</span>
              <el-button type="success" size="small" @click="showAddGrowthDialog">添加记录</el-button>
            </div>
          </template>
          <el-table :data="growthList" style="width: 100%">
            <el-table-column prop="measureDate" label="测量日期" width="150" />
            <el-table-column prop="height" label="身高(cm)" width="120" />
            <el-table-column prop="weight" label="体重(kg)" width="120" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="deleteGrowthRecord(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="growthList.length === 0" description="暂无成长记录" />
        </el-card>
      </div>

      <el-empty v-else description="请选择幼儿" />
    </el-card>

    <!-- 编辑健康信息对话框 -->
    <el-dialog v-model="healthDialogVisible" title="编辑健康信息" width="500px">
      <el-form :model="healthForm" label-width="100px">
        <el-form-item label="血型">
          <el-select v-model="healthForm.bloodType" placeholder="请选择血型">
            <el-option label="A型" value="A" />
            <el-option label="B型" value="B" />
            <el-option label="AB型" value="AB" />
            <el-option label="O型" value="O" />
            <el-option label="未知" value="Unknown" />
          </el-select>
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input v-model="healthForm.allergyInfo" type="textarea" :rows="3" placeholder="请输入过敏史" />
        </el-form-item>
        <el-form-item label="病史">
          <el-input v-model="healthForm.medicalHistory" type="textarea" :rows="3" placeholder="请输入病史" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="healthDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveHealthInfo">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加体温对话框 -->
    <el-dialog v-model="tempDialogVisible" title="添加体温记录" width="500px">
      <el-form :model="tempForm" label-width="100px">
        <el-form-item label="测量时间">
          <el-date-picker
            v-model="tempForm.measureTime"
            type="datetime"
            placeholder="选择日期时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="体温(°C)">
          <el-input-number v-model="tempForm.temperature" :min="35" :max="42" :step="0.1" :precision="1" />
        </el-form-item>
        <el-form-item label="时段">
          <el-select v-model="tempForm.period" placeholder="请选择时段">
            <el-option label="早上" value="morning" />
            <el-option label="中午" value="noon" />
            <el-option label="下午" value="afternoon" />
            <el-option label="晚上" value="evening" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="tempForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tempDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTempRecord">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加成长记录对话框 -->
    <el-dialog v-model="growthDialogVisible" title="添加成长记录" width="500px">
      <el-form :model="growthForm" label-width="100px">
        <el-form-item label="测量日期">
          <el-date-picker
            v-model="growthForm.measureDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="身高(cm)">
          <el-input-number v-model="growthForm.height" :min="50" :max="200" :step="0.1" :precision="1" />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="growthForm.weight" :min="5" :max="100" :step="0.1" :precision="1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="growthForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="growthDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGrowthRecord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getChildList,
  getHealthInfo,
  updateHealthInfo,
  getTemperatureRecords,
  recordTemperature,
  deleteTemperatureRecord,
  getGrowthRecords,
  recordGrowth,
  deleteGrowthRecord
} from '@/api/admin'

// 数据
const childrenList = ref([])
const selectedChildId = ref(null)
const healthInfo = ref({})
const temperatureList = ref([])
const growthList = ref([])

// 对话框
const healthDialogVisible = ref(false)
const tempDialogVisible = ref(false)
const growthDialogVisible = ref(false)

// 表单
const healthForm = reactive({
  bloodType: '',
  allergyInfo: '',
  medicalHistory: ''
})

const tempForm = reactive({
  measureTime: '',
  temperature: 36.5,
  period: 'morning',
  remark: ''
})

const growthForm = reactive({
  measureDate: '',
  height: null,
  weight: null,
  remark: ''
})

// 获取幼儿列表
const fetchChildren = async () => {
  try {
    const res = await getChildList({ pageNum: 1, pageSize: 1000 })
    childrenList.value = res.data.records || []
  } catch (error) {
    ElMessage.error('获取幼儿列表失败')
  }
}

// 加载健康数据
const loadHealthData = async () => {
  if (!selectedChildId.value) return

  try {
    // 获取健康基本信息
    const healthRes = await getHealthInfo(selectedChildId.value)
    healthInfo.value = healthRes.data || {}

    // 获取体温记录
    const tempRes = await getTemperatureRecords(selectedChildId.value, {})
    temperatureList.value = tempRes.data || []

    // 获取成长记录
    const growthRes = await getGrowthRecords(selectedChildId.value)
    growthList.value = growthRes.data || []
  } catch (error) {
    ElMessage.error('加载健康数据失败')
  }
}

// 显示编辑健康信息对话框
const showEditHealthDialog = () => {
  healthForm.bloodType = healthInfo.value.bloodType || ''
  healthForm.allergyInfo = healthInfo.value.allergyInfo || ''
  healthForm.medicalHistory = healthInfo.value.medicalHistory || ''
  healthDialogVisible.value = true
}

// 保存健康信息
const saveHealthInfo = async () => {
  try {
    await updateHealthInfo(selectedChildId.value, healthForm)
    ElMessage.success('保存成功')
    healthDialogVisible.value = false
    loadHealthData()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 显示添加体温对话框
const showAddTempDialog = () => {
  tempForm.measureTime = new Date().toISOString().slice(0, 19)
  tempForm.temperature = 36.5
  tempForm.period = 'morning'
  tempForm.remark = ''
  tempDialogVisible.value = true
}

// 保存体温记录
const saveTempRecord = async () => {
  if (!tempForm.measureTime || !tempForm.temperature) {
    ElMessage.warning('请填写必填项')
    return
  }

  try {
    await recordTemperature(selectedChildId.value, {
      ...tempForm,
      recorderId: 1 // TODO: 获取当前用户ID
    })
    ElMessage.success('添加成功')
    tempDialogVisible.value = false
    loadHealthData()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

// 删除体温记录
const deleteTempRecord = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条体温记录吗?', '提示', { type: 'warning' })
    await deleteTemperatureRecord(id)
    ElMessage.success('删除成功')
    loadHealthData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 显示添加成长记录对话框
const showAddGrowthDialog = () => {
  growthForm.measureDate = new Date().toISOString().slice(0, 10)
  growthForm.height = null
  growthForm.weight = null
  growthForm.remark = ''
  growthDialogVisible.value = true
}

// 保存成长记录
const saveGrowthRecord = async () => {
  if (!growthForm.measureDate) {
    ElMessage.warning('请选择测量日期')
    return
  }

  try {
    await recordGrowth(selectedChildId.value, {
      ...growthForm,
      recorderId: 1 // TODO: 获取当前用户ID
    })
    ElMessage.success('添加成功')
    growthDialogVisible.value = false
    loadHealthData()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

// 删除成长记录
const deleteGrowthRecord = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条成长记录吗?', '提示', { type: 'warning' })
    await deleteGrowthRecord(id)
    ElMessage.success('删除成功')
    loadHealthData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 体温状态
const getTempType = (temp) => {
  if (temp < 36) return 'info'
  if (temp >= 37.3) return 'danger'
  if (temp >= 37) return 'warning'
  return 'success'
}

onMounted(() => {
  fetchChildren()
})
</script>

<style scoped>
.health-container {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
