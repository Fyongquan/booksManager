<!--
  用户个人信息组件
  功能：
  - 显示/编辑用户信息
  - 头像上传
  - 密码修改
  - 表单验证
-->
<template>
  <el-dialog title="个人信息" v-model="dialogVisible" width="500px">
    <el-form
      :model="userForm"
      :rules="rules"
      ref="userFormRef"
      label-width="80px"
    >
      <!-- 头像部分 -->
      <div class="avatar-container">
        <el-upload
          class="avatar-uploader"
          :http-request="customUpload"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
        >
          <el-avatar
            :size="100"
            :src="
              userForm.avatar
                ? `http://localhost:8081${userForm.avatar}`
                : defaultAvatar
            "
          />
          <div class="avatar-hover-text">点击更换头像</div>
        </el-upload>
      </div>

      <!-- 表单部分 -->
      <el-form-item label="账号">
        <el-input v-model="userForm.loginCode" disabled />
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="userForm.userName" />
      </el-form-item>
      <el-form-item label="邮箱" prop="userEmail">
        <el-input v-model="userForm.userEmail" />
      </el-form-item>
      <el-form-item label="手机号" prop="userPhone">
        <el-input v-model="userForm.userPhone" />
      </el-form-item>
      <el-form-item label="用户类型">
        <el-input v-model="userForm.userType" disabled />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
        <el-button type="warning" @click="showPasswordDialog"
          >修改密码</el-button
        >
      </span>
    </template>

    <!-- 修改密码的对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="30%"
      append-to-body
    >
      <el-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handlePasswordSubmit">
            确 定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, inject } from "vue"
import { ElMessage } from "element-plus"
import { useRouter } from "vue-router"
import { Plus } from "@element-plus/icons-vue"
import defaultAvatar from "@/assets/default-avatar.png"
import { userApi } from "@/api/user"

const router = useRouter()
const dialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const userFormRef = ref()
const passwordFormRef = ref()

const userForm = reactive({
  userId: "",
  loginCode: "",
  userName: "",
  userEmail: "",
  userPhone: "",
  userType: "",
  avatar: "",
})

const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
})

const rules = {
  userName: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur" },
  ],
  userEmail: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
  ],
  userPhone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
}

const passwordRules = {
  oldPassword: [{ required: true, message: "请输入原密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"))
        } else {
          callback()
        }
      },
      trigger: "blur",
    },
  ],
}

const baseURL = import.meta.env.VITE_API_URL
const uploadHeaders = computed(() => ({
  token: localStorage.getItem("token"),
}))

const updateUserInfo = inject("updateUserInfo")

const showDialog = (userData) => {
  if (userData) {
    Object.assign(userForm, userData)
    console.log("Dialog form data:", userForm)
  }
  dialogVisible.value = true
}

const handleClose = () => {
  dialogVisible.value = false
  userFormRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await userApi.profile(userForm)
        if (res.msgResult === "success") {
          ElMessage.success("修改成功")
          dialogVisible.value = false
          localStorage.setItem("userInfo", JSON.stringify(res.data))
          updateUserInfo(res.data)
        } else {
          ElMessage.error(res.msgInfo || "修改失败")
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msgInfo || "修改失败")
      }
    }
  })
}

const showPasswordDialog = () => {
  passwordDialogVisible.value = true
}

const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await userApi.updatePassword({
          userId: userForm.userId,
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
        })
        if (res.msgResult === "success") {
          ElMessage.success("密码修改成功，请重新登录")
          passwordDialogVisible.value = false
          dialogVisible.value = false
          localStorage.removeItem("token")
          localStorage.removeItem("userInfo")
          router.push("/login")
        } else {
          ElMessage.error(res.msgInfo || "修改失败")
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.msgInfo || "修改失败")
      }
    }
  })
}

const customUpload = async (options) => {
  try {
    const res = await userApi.uploadAvatar(userForm.userId, options.file)
    if (res.msgResult === "success") {
      userForm.avatar = res.data
      const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}")
      const updatedUserInfo = {
        ...userInfo,
        avatar: res.data,
      }
      localStorage.setItem("userInfo", JSON.stringify(updatedUserInfo))
      updateUserInfo(updatedUserInfo)
      ElMessage.success("头像上传成功")
    } else {
      ElMessage.error(res.msgInfo || "上传失败")
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msgInfo || "上传失败")
    console.error("上传失败:", error)
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = ["image/jpeg", "image/png", "image/gif"].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error("上传头像图片只能是 JPG/PNG/GIF 格式!")
  }
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB!")
  }
  return isImage && isLt2M
}

const emit = defineEmits(["update-user-info"])

defineExpose({
  showDialog,
})
</script>

<style scoped>
.avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar-hover-text {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  text-align: center;
  padding: 4px 0;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-uploader:hover .avatar-hover-text {
  opacity: 1;
}

.el-avatar {
  display: block;
  margin: 0 auto;
}
</style>
