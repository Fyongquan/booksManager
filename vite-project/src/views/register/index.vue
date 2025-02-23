<template>
  <div class="register-container">
    <el-form
      ref="registerFormRef"
      :model="registerForm"
      :rules="rules"
      class="register-form"
      label-width="80px"
    >
      <h3 class="title">图书管理系统 - 用户注册</h3>
      <el-form-item label="账号" prop="loginCode">
        <el-input
          v-model="registerForm.loginCode"
          placeholder="请输入账号"
          type="text"
        />
      </el-form-item>
      <el-form-item label="密码" prop="loginPwd">
        <el-input
          v-model="registerForm.loginPwd"
          placeholder="请输入密码"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPwd">
        <el-input
          v-model="registerForm.confirmPwd"
          placeholder="请确认密码"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="registerForm.userName"
          placeholder="请输入用户名"
          type="text"
        />
      </el-form-item>
      <el-form-item label="邮箱" prop="userEmail">
        <el-input
          v-model="registerForm.userEmail"
          placeholder="请输入邮箱"
          type="email"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="userPhone">
        <el-input
          v-model="registerForm.userPhone"
          placeholder="请输入手机号"
          type="text"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :loading="loading"
          class="submit-btn"
          @click="handleRegister"
        >
          注册
        </el-button>
        <el-button @click="goToLogin">返回登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import { userApi } from "@/api/user"

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = ref({
  loginCode: "",
  loginPwd: "",
  confirmPwd: "",
  userName: "",
  userEmail: "",
  userPhone: "",
  userType: "user",
  roleType: "USER",
})

const validatePass = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请输入密码"))
  } else {
    if (registerForm.value.confirmPwd !== "") {
      registerFormRef.value.validateField("confirmPwd")
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"))
  } else if (value !== registerForm.value.loginPwd) {
    callback(new Error("两次输入密码不一致!"))
  } else {
    callback()
  }
}

const rules = {
  loginCode: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  loginPwd: [
    { required: true, validator: validatePass, trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
  ],
  confirmPwd: [{ required: true, validator: validatePass2, trigger: "blur" }],
  userName: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  userEmail: [
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
  ],
  userPhone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
}

const handleRegister = async () => {
  console.log("注册按钮被点击")
  if (!registerFormRef.value) return
  console.log("开始表单验证")

  await registerFormRef.value.validate(async (valid) => {
    console.log("表单验证结果:", valid)
    if (valid) {
      loading.value = true
      try {
        const { confirmPwd, ...registerData } = registerForm.value
        console.log("准备发送注册数据:", JSON.stringify(registerData))
        const res = await userApi.register(registerData)
        console.log("注册响应:", res)
        if (res.msgResult === "success") {
          ElMessage.success(res.msgInfo || "注册成功")
          router.push("/login")
        } else {
          ElMessage.error(res.msgInfo || "注册失败")
        }
      } catch (error) {
        console.error("注册失败:", error)
        ElMessage.error(
          error.response?.data?.msgInfo || error.message || "注册失败"
        )
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push("/login")
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;

  .register-form {
    width: 400px;
    padding: 30px;
    background-color: white;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .title {
      text-align: center;
      margin-bottom: 30px;
    }

    .submit-btn {
      width: 100%;
      margin-bottom: 10px;
    }
  }
}
</style>
