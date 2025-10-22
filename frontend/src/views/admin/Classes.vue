<template>
  <div class="classes-container">
    <h2>班级管理</h2>
    <el-card>
      <el-row style="margin-bottom: 20px">
        <el-button type="primary" @click="showAddDialog">新增班级</el-button>
      </el-row>

      <el-table :data="classes" style="width: 100%">
        <el-table-column prop="className" label="班级名称" width="150" />
        <el-table-column prop="gradeLevel" label="年级" width="100" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="currentCount" label="当前人数" width="100" />
        <el-table-column prop="teacherNames" label="班主任" width="150" />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column prop="description" label="班级描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="editClass(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteClass(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchClasses"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="班级名称">
          <el-input v-model="form.className" placeholder="例如：小班1班" />
        </el-form-item>
        <el-form-item label="年级">
          <el-select v-model="form.gradeLevel" placeholder="请选择年级" style="width: 100%">
            <el-option label="托班" value="托班" />
            <el-option label="小班" value="小班" />
            <el-option label="中班" value="中班" />
            <el-option label="大班" value="大班" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级容量">
          <el-input-number v-model="form.capacity" :min="10" :max="50" />
        </el-form-item>
        <el-form-item label="教室">
          <el-input v-model="form.classroom" placeholder="例如：一楼东侧第一间" />
        </el-form-item>
        <el-form-item label="班主任">
          <el-select v-model="form.teacherIds" multiple placeholder="请选择班主任" style="width: 100%">
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.id"
              :label="teacher.realName"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveClass">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getClassList, addClass, updateClass, deleteClass as delClass, getUserList } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const classes = ref([])
const teacherList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('新增班级')
const form = ref({
  className: '',
  gradeLevel: '',
  capacity: 30,
  classroom: '',
  teacherIds: [],
  description: ''
})

const fetchClasses = async () => {
  try {
    const res = await getClassList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    classes.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取班级列表失败')
  }
}

const fetchTeacherList = async () => {
  try {
    const res = await getUserList({ role: 'TEACHER', pageNum: 1, pageSize: 100 })
    teacherList.value = res.data.records || []
  } catch (error) {
    console.error('获取教师列表失败')
  }
}

const showAddDialog = () => {
  dialogTitle.value = '新增班级'
  form.value = {
    className: '',
    gradeLevel: '',
    capacity: 30,
    classroom: '',
    teacherIds: [],
    description: ''
  }
  dialogVisible.value = true
}

const editClass = (row) => {
  dialogTitle.value = '编辑班级'
  form.value = {
    ...row,
    teacherIds: row.teacherIds || []
  }
  dialogVisible.value = true
}

const saveClass = async () => {
  if (!form.value.className || !form.value.gradeLevel) {
    ElMessage.warning('请填写必填项')
    return
  }

  try {
    if (form.value.id) {
      await updateClass(form.value.id, form.value)
      ElMessage.success('班级信息更新成功')
    } else {
      await addClass(form.value)
      ElMessage.success('班级信息创建成功')
    }
    dialogVisible.value = false
    fetchClasses()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteClass = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该班级吗？', '提示', {
      type: 'warning'
    })
    await delClass(id)
    ElMessage.success('删除成功')
    fetchClasses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchClasses()
  fetchTeacherList()
})
</script>

<style scoped>
.classes-container {
  padding: 20px;
}
</style>
