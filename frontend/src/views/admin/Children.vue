<template>
  <div class="children-container">
    <h2>幼儿管理</h2>
    <el-card>
      <el-row style="margin-bottom: 20px">
        <el-button type="primary" @click="showAddDialog">新增幼儿</el-button>
      </el-row>

      <el-table :data="children" style="width: 100%">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 'M' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="parentName" label="家长" width="100" />
        <el-table-column prop="parentPhone" label="家长电话" width="130" />
        <el-table-column prop="address" label="家庭住址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="enrollmentDate" label="入园日期" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="editChild(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteChild(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="fetchChildren"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="M">男</el-radio>
            <el-radio label="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.className"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="家长">
          <el-select v-model="form.parentId" placeholder="请选择家长" style="width: 100%">
            <el-option
              v-for="parent in parentList"
              :key="parent.id"
              :label="`${parent.realName} (${parent.phone})`"
              :value="parent.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="家庭住址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="入园日期">
          <el-date-picker
            v-model="form.enrollmentDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveChild">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getChildList, addChild, updateChild, deleteChild as delChild, getClassList, getUserList } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const children = ref([])
const classList = ref([])
const parentList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('新增幼儿')
const form = ref({
  name: '',
  gender: 'M',
  birthDate: '',
  classId: '',
  parentId: '',
  address: '',
  enrollmentDate: ''
})

const fetchChildren = async () => {
  try {
    const res = await getChildList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    children.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取幼儿列表失败')
  }
}

const fetchClassList = async () => {
  try {
    const res = await getClassList({ pageNum: 1, pageSize: 100 })
    classList.value = res.data.records || []
  } catch (error) {
    console.error('获取班级列表失败')
  }
}

const fetchParentList = async () => {
  try {
    const res = await getUserList({ role: 'PARENT', pageNum: 1, pageSize: 100 })
    parentList.value = res.data.records || []
  } catch (error) {
    console.error('获取家长列表失败')
  }
}

const showAddDialog = () => {
  dialogTitle.value = '新增幼儿'
  form.value = {
    name: '',
    gender: 'M',
    birthDate: '',
    classId: '',
    parentId: '',
    address: '',
    enrollmentDate: ''
  }
  dialogVisible.value = true
}

const editChild = (row) => {
  dialogTitle.value = '编辑幼儿'
  form.value = { ...row }
  dialogVisible.value = true
}

const saveChild = async () => {
  if (!form.value.name || !form.value.birthDate) {
    ElMessage.warning('请填写必填项')
    return
  }

  try {
    if (form.value.id) {
      await updateChild(form.value.id, form.value)
      ElMessage.success('幼儿信息更新成功')
    } else {
      await addChild(form.value)
      ElMessage.success('幼儿信息创建成功')
    }
    dialogVisible.value = false
    fetchChildren()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteChild = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该幼儿吗？', '提示', {
      type: 'warning'
    })
    await delChild(id)
    ElMessage.success('删除成功')
    fetchChildren()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchChildren()
  fetchClassList()
  fetchParentList()
})
</script>

<style scoped>
.children-container {
  padding: 20px;
}
</style>
