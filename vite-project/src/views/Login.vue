<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>简易图书管理系统</h2>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item>
          <el-radio-group v-model="loginType" @change="handleLoginTypeChange">
            <el-radio label="account">账号登录</el-radio>
            <el-radio label="phone">手机号登录</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="loginCode">
          <el-input
            v-model="loginForm.loginCode"
            placeholder="请输入用户名"
            type="text"
            tabindex="1"
            autocomplete="on"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="loginPwd">
          <el-input
            v-model="loginForm.loginPwd"
            placeholder="请输入密码"
            :type="passwordVisible ? 'text' : 'password'"
            tabindex="2"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="imageCode">
          <div class="verify-code">
            <el-input
              v-model="loginForm.imageCode"
              placeholder="请输入验证码"
              tabindex="3"
            >
              <template #prefix>
                <el-icon><Picture /></el-icon>
              </template>
            </el-input>
            <img :src="captchaImg" class="captcha-img" @click="getCaptcha" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleLogin"
            :loading="loading"
            style="width: 100%; margin-bottom: 10px"
          >
            登录
          </el-button>
          <el-button
            @click="goToRegister"
            style="width: 100%; margin-left: -1px"
          >
            注册账号
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import { ElMessage } from "element-plus"
import { loginApi } from "@/api/login"
import { setCookie, getCookie, removeCookie } from "@/utils/cookie"

const router = useRouter()
const route = useRoute()
const loginFormRef = ref(null)
const loading = ref(false)
const captchaImg = ref("")
const uuid = ref("")

const loginForm = ref({
  loginCode: "",
  loginPwd: "",
  uuid: "",
  imageCode: "",
})

const loginType = ref("account")

const rules = {
  loginCode: [
    { required: true, message: "请输入账号", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (loginType.value === "phone") {
          if (!/^1[3-9]\d{9}$/.test(value)) {
            callback(new Error("请输入正确的手机号"))
          }
        }
        callback()
      },
      trigger: "blur",
    },
  ],
  loginPwd: [{ required: true, message: "请输入密码", trigger: "blur" }],
  imageCode: [{ required: true, message: "请输入验证码", trigger: "blur" }],
}

const rememberPassword = ref(false)

// 获取验证码
const getCaptcha = async () => {
  try {
    const res = await loginApi.getCode()
    if (res.msgResult === "success") {
      captchaImg.value = `data:image/png;base64,${res.data.base64}`
      loginForm.value.uuid = res.data.uuid
    } else {
      ElMessage.error("获取验证码失败")
    }
  } catch (error) {
    console.error("获取验证码失败:", error)
    ElMessage.error("获取验证码失败")
  }
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await loginApi.check({
          loginCode: loginForm.value.loginCode,
          loginPwd: loginForm.value.loginPwd,
          uuid: loginForm.value.uuid,
          imageCode: loginForm.value.imageCode,
        })

        if (res.message === "success") {
          // 保存登录信息到cookie
          if (rememberPassword.value) {
            const loginInfo = {
              loginCode: loginForm.value.loginCode,
              loginPwd: loginForm.value.loginPwd,
              remember: true,
            }
            setCookie("loginInfo", JSON.stringify(loginInfo), 7)
          } else {
            removeCookie("loginInfo")
          }

          localStorage.setItem("token", res.token)
          localStorage.setItem("loginInfo", JSON.stringify(res.info))
          ElMessage.success("登录成功")

          // 获取重定向路径
          const redirect = route.query?.redirect
          router.push(redirect || "/home/dashboard")
        } else {
          ElMessage.error(res.info || "登录失败")
          getCaptcha()
        }
      } catch (error) {
        console.error("登录失败:", error)
        ElMessage.error(error.response?.data?.info || "登录失败")
        getCaptcha()
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push("/register")
}

const handleLoginTypeChange = () => {
  loginForm.value.loginCode = ""
  if (loginFormRef.value) {
    loginFormRef.value.clearValidate("loginCode")
  }
  // 清除cookie中的登录信息
  removeCookie("loginInfo")
  rememberPassword.value = false
}

onMounted(() => {
  getCaptcha()
  // 从cookie获取保存的登录信息
  const savedLoginInfo = getCookie("loginInfo")
  if (savedLoginInfo) {
    try {
      const { loginCode, loginPwd, remember } = JSON.parse(savedLoginInfo)
      if (remember) {
        loginForm.value.loginCode = loginCode
        loginForm.value.loginPwd = loginPwd
        rememberPassword.value = true
      }
    } catch (error) {
      console.error("解析登录信息失败:", error)
      removeCookie("loginInfo")
    }
  }
})

// 退出登录时清除cookie
const clearSavedPassword = () => {
  removeCookie("loginInfo")
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;

  .login-card {
    width: 400px;

    :deep(.el-card__header) {
      text-align: center;

      h2 {
        margin: 0;
        color: #409eff;
      }
    }
  }

  .verify-code {
    display: flex;
    gap: 10px;
    align-items: center;

    .el-input {
      flex: 1;
    }

    .captcha-img {
      height: 40px;
      cursor: pointer;
      margin-left: 10px;
      border-radius: 4px;
    }
  }

  .login-button,
  .register-button {
    width: 100%;
    margin-bottom: 10px;
  }
}
</style>
