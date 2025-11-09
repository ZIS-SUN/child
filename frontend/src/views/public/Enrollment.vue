<template>
  <div class="enrollment-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">在线报名</h1>
        <p class="page-subtitle">开启孩子的快乐成长之旅</p>
      </div>

      <div class="enrollment-container">
        <el-card class="enrollment-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-icon">📝</span>
              <span>报名信息</span>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-width="120px"
            size="large"
          >
            <!-- 孩子信息 -->
            <div class="form-section">
              <h3 class="section-title">孩子信息</h3>
              <el-form-item label="姓名" prop="childName">
                <el-input v-model="formData.childName" placeholder="请输入孩子姓名" />
              </el-form-item>

              <el-form-item label="性别" prop="childGender">
                <el-radio-group v-model="formData.childGender">
                  <el-radio label="男">男孩</el-radio>
                  <el-radio label="女">女孩</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="出生日期" prop="childBirthday">
                <el-date-picker
                  v-model="formData.childBirthday"
                  type="date"
                  placeholder="请选择出生日期"
                  style="width: 100%"
                  :disabled-date="disabledDate"
                />
              </el-form-item>

              <el-form-item label="身份证号" prop="childIdCard">
                <el-input v-model="formData.childIdCard" placeholder="请输入孩子身份证号" />
              </el-form-item>
            </div>

            <!-- 家长信息 -->
            <div class="form-section">
              <h3 class="section-title">家长信息</h3>
              <el-form-item label="家长姓名" prop="parentName">
                <el-input v-model="formData.parentName" placeholder="请输入家长姓名" />
              </el-form-item>

              <el-form-item label="与孩子关系" prop="relationship">
                <el-select v-model="formData.relationship" placeholder="请选择" style="width: 100%">
                  <el-option label="父亲" value="父亲" />
                  <el-option label="母亲" value="母亲" />
                  <el-option label="爷爷" value="爷爷" />
                  <el-option label="奶奶" value="奶奶" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>

              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="formData.phone" placeholder="请输入联系电话" />
              </el-form-item>

              <el-form-item label="家庭地址" prop="address">
                <el-input
                  v-model="formData.address"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入家庭地址"
                />
              </el-form-item>
            </div>

            <!-- 报名信息 -->
            <div class="form-section">
              <h3 class="section-title">报名信息</h3>
              <el-form-item label="意向班级" prop="intentClass">
                <el-select v-model="formData.intentClass" placeholder="请选择意向班级" style="width: 100%">
                  <el-option label="小班（3-4岁）" value="小班" />
                  <el-option label="中班（4-5岁）" value="中班" />
                  <el-option label="大班（5-6岁）" value="大班" />
                </el-select>
              </el-form-item>

              <el-form-item label="备注说明" prop="remark">
                <el-input
                  v-model="formData.remark"
                  type="textarea"
                  :rows="4"
                  placeholder="如有特殊情况或需要说明的事项，请在此填写"
                />
              </el-form-item>

              <el-form-item label="相关附件">
                <el-upload
                  ref="uploadRef"
                  :action="uploadAction"
                  :on-success="handleUploadSuccess"
                  :on-error="handleUploadError"
                  :file-list="fileList"
                  :limit="3"
                  accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                >
                  <el-button :icon="Upload">上传附件</el-button>
                  <template #tip>
                    <div class="upload-tip">
                      支持PDF、Word、图片格式，单个文件不超过5MB，最多3个文件
                    </div>
                  </template>
                </el-upload>
              </el-form-item>
            </div>

            <el-form-item>
              <div class="form-actions">
                <el-button @click="handleReset" size="large">重置</el-button>
                <el-button type="primary" @click="handleSubmit" :loading="submitting" size="large">
                  提交报名
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 温馨提示 -->
        <el-card class="tips-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-icon">💡</span>
              <span>温馨提示</span>
            </div>
          </template>
          <div class="tips-content">
            <h4>报名须知：</h4>
            <ol>
              <li>请确保填写的信息真实有效，我们会在3个工作日内与您联系</li>
              <li>报名成功后，请保持电话畅通，以便我们及时与您沟通</li>
              <li>可提前预约参观幼儿园，了解教学环境和师资情况</li>
              <li>需要准备的材料：户口本、出生证明、预防接种证等</li>
            </ol>
            <h4>联系方式：</h4>
            <p>📞 咨询电话：0123-12345678</p>
            <p>📧 邮箱：contact@ultrathink.com</p>
            <p>🕐 咨询时间：周一至周五 9:00-17:00</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { submitEnrollment } from '@/api/public'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

const formRef = ref(null)
const uploadRef = ref(null)
const submitting = ref(false)
const uploadAction = ref(import.meta.env.VITE_API_BASE_URL + '/public/upload')

const formData = reactive({
  childName: '',
  childGender: '',
  childBirthday: '',
  childIdCard: '',
  parentName: '',
  relationship: '',
  phone: '',
  address: '',
  intentClass: '',
  remark: '',
  attachments: []
})

const fileList = ref([])

const rules = {
  childName: [
    { required: true, message: '请输入孩子姓名', trigger: 'blur' }
  ],
  childGender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  childBirthday: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  childIdCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dxX]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  parentName: [
    { required: true, message: '请输入家长姓名', trigger: 'blur' }
  ],
  relationship: [
    { required: true, message: '请选择与孩子关系', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入家庭地址', trigger: 'blur' }
  ],
  intentClass: [
    { required: true, message: '请选择意向班级', trigger: 'change' }
  ]
}

const disabledDate = (time) => {
  // 只能选择3-6年前的日期
  const now = new Date()
  const threeYearsAgo = new Date(now.getFullYear() - 3, now.getMonth(), now.getDate())
  const sixYearsAgo = new Date(now.getFullYear() - 6, now.getMonth(), now.getDate())
  return time.getTime() > threeYearsAgo.getTime() || time.getTime() < sixYearsAgo.getTime()
}

const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    formData.attachments.push(response.data.url)
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error('文件上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('文件上传失败，请重试')
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await ElMessageBox.confirm(
          '请确认信息填写无误，提交后我们会尽快与您联系。',
          '确认提交',
          {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'info'
          }
        )

        submitting.value = true
        // 调用API
        // await submitEnrollment(formData)

        // 模拟提交
        await new Promise(resolve => setTimeout(resolve, 1500))

        ElMessage.success({
          message: '报名成功！我们会在3个工作日内与您联系',
          duration: 5000
        })

        // 重置表单
        handleReset()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('提交失败，请重试')
        }
      } finally {
        submitting.value = false
      }
    } else {
      ElMessage.warning('请完整填写必填信息')
    }
  })
}

const handleReset = () => {
  formRef.value?.resetFields()
  fileList.value = []
  formData.attachments = []
}
</script>

<style scoped>
.enrollment-page {
  min-height: 100vh;
  padding: 40px 0;
  background: linear-gradient(to bottom, #f5f7fa 0%, #ffffff 100%);
}

.container {
  max-width: 1000px;
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

.enrollment-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  align-items: start;
}

.enrollment-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.header-icon {
  font-size: 24px;
}

.form-section {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #eee;
}

.form-section:last-of-type {
  border-bottom: none;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 18px;
  background: linear-gradient(to bottom, #667eea, #764ba2);
  margin-right: 12px;
  border-radius: 2px;
}

.upload-tip {
  font-size: 13px;
  color: #7f8c8d;
  margin-top: 8px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}

.form-actions .el-button {
  min-width: 120px;
}

.tips-card {
  border-radius: 16px;
  position: sticky;
  top: 90px;
}

.tips-content h4 {
  font-size: 16px;
  color: #2c3e50;
  margin: 16px 0 12px;
}

.tips-content h4:first-child {
  margin-top: 0;
}

.tips-content ol {
  padding-left: 20px;
  margin: 0 0 16px;
}

.tips-content li {
  font-size: 14px;
  color: #5a6c7d;
  line-height: 1.8;
  margin-bottom: 8px;
}

.tips-content p {
  font-size: 14px;
  color: #5a6c7d;
  line-height: 1.8;
  margin: 8px 0;
}

@media (max-width: 1024px) {
  .enrollment-container {
    grid-template-columns: 1fr;
  }

  .tips-card {
    position: static;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }

  .enrollment-card :deep(.el-form-item__label) {
    width: 100% !important;
    text-align: left;
  }

  .enrollment-card :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
}
</style>
