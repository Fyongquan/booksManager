<!--
  布局组件
  功能：
  - 整体页面布局
  - 导航菜单
  - 用户信息展示
  - 权限控制
-->
<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">图书管理系统</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/home/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 管理员菜单 -->
        <template v-if="userInfo.roleType === 'ADMIN'">
          <el-menu-item index="/home/books">
            <el-icon><Reading /></el-icon>
            <span>图书管理</span>
          </el-menu-item>

          <el-menu-item index="/home/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <el-menu-item index="/home/borrow">
            <el-icon><List /></el-icon>
            <span>借阅管理</span>
          </el-menu-item>
        </template>

        <!-- 普通用户菜单 -->
        <template v-else>
          <el-menu-item index="/home/borrowBooks">
            <el-icon><List /></el-icon>
            <span>借阅图书</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar
                :size="32"
                :src="
                  userInfo.avatar
                    ? `http://localhost:8081${userInfo.avatar}`
                    : defaultAvatar
                "
              />
              {{ userInfo.userName }}
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>

    <!-- 个人信息组件 -->
    <user-profile ref="userProfileRef" />
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch, provide } from "vue"
import { useRouter, useRoute } from "vue-router"
import { ElMessage, ElMessageBox } from "element-plus"
import UserProfile from "@/components/UserProfile.vue"
import { userApi } from "@/api/user"
import defaultAvatar from "@/assets/default-avatar.png"

const router = useRouter()
const route = useRoute()
const userProfileRef = ref()

// 用户信息
const userInfo = ref({
  userName: "",
  roleType: "",
  avatar: "",
})

// 提供更新用户信息的方法
const updateUserInfo = (newInfo) => {
  Object.assign(userInfo.value, newInfo)
}

// 提供给子组件使用
provide("updateUserInfo", updateUserInfo)

// 获取用户信息
const getUserInfo = async () => {
  try {
    const res = await userApi.getUserInfo()
    if (res.msgResult === "success") {
      userInfo.value = res.data
      console.log("Current user info:", userInfo.value) // 添加日志
      // 如果是普通用户且当前路由是管理员专属路由，则重定向到借阅图书页面
      if (
        userInfo.value.roleType !== "ADMIN" &&
        ["/home/books", "/home/users", "/home/borrow"].includes(route.path)
      ) {
        router.push("/home/borrowBooks")
      }
    } else if (res.msgResult === "error" && res.msgInfo === "token无效") {
      // token 无效，跳转到登录页
      ElMessage.warning("登录已过期，请重新登录")
      localStorage.removeItem("token")
      localStorage.removeItem("loginInfo")
      router.push("/login")
    }
  } catch (error) {
    console.error("获取用户信息失败:", error)
    if (error.response?.status === 401) {
      router.push("/login")
    }
  }
}

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === "logout") {
    handleLogout()
  } else if (command === "profile") {
    console.log("Opening profile dialog", userInfo.value)
    userProfileRef.value?.showDialog(userInfo.value)
  }
}

const handleLogout = () => {
  ElMessageBox.confirm("确定要退出登录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      // 清除 token
      localStorage.removeItem("token")
      localStorage.removeItem("userInfo")
      // 跳转到登录页
      router.push("/login")
      ElMessage.success("已退出登录")
    })
    .catch(() => {
      // 取消退出
    })
}

onMounted(async () => {
  await getUserInfo() // 等待获取用户信息完成
})
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;

  .el-aside {
    background-color: #304156;
    color: #fff;

    .logo {
      height: 60px;
      line-height: 60px;
      text-align: center;
      font-size: 20px;
      font-weight: bold;
      color: #fff;
      background-color: #2b2f3a;
    }
  }

  .el-header {
    background-color: #fff;
    border-bottom: 1px solid #dcdfe6;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 0 20px;

    .header-right {
      .user-info {
        cursor: pointer;
        color: #606266;
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .el-main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style>
