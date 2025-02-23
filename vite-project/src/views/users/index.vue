<template>
  <div class="users-container">
    <el-card>
      <template #header>
        <div class="header">
          <div class="left">
            <el-input
              v-model="queryParams.queryText"
              placeholder="请输入用户名或账号"
              style="width: 200px"
              clearable
              @input="handleQueryTextChange"
              @clear="handleQuery"
            />
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>搜索
            </el-button>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增用户
          </el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="userList" stripe border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="loginCode" label="账号" />
        <el-table-column prop="userName" label="用户名" />
        <el-table-column prop="userEmail" label="邮箱" />
        <el-table-column prop="userPhone" label="手机号" />
        <el-table-column prop="userType" label="用户类型">
          <template #default="{ row }">
            <el-tag :type="row.userType === 'admin' ? 'success' : 'info'">
              {{ row.userType === "admin" ? "管理员" : "普通用户" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gmtCreate" label="创建时间" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="primary" link @click="handleResetPwd(row)">
              重置密码
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="账号" prop="loginCode">
          <el-input
            v-model="userForm.loginCode"
            :disabled="dialogType === 'edit'"
          />
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
        <el-form-item label="密码" prop="loginPwd" v-if="dialogType === 'add'">
          <el-input v-model="userForm.loginPwd" type="password" show-password />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="userForm.userType" placeholder="请选择用户类型">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="submitLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue"
import { Search, Plus } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { userApi } from "@/api/user"

// 查询参数
const queryParams = ref({
  queryText: "",
  pageNum: 1,
  pageSize: 10,
})

const loading = ref(false)
const userList = ref([])
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref("add") // add or edit
const dialogTitle = computed(() =>
  dialogType.value === "add" ? "新增用户" : "编辑用户"
)
const userFormRef = ref(null)
const submitLoading = ref(false)

const userForm = ref({
  userId: undefined,
  loginCode: "",
  loginPwd: "",
  userName: "",
  userEmail: "",
  userPhone: "",
  userType: "user", // 默认为普通用户
  roleType: "USER", // 默认为普通用户角色
})

// 表单验证规则
const rules = {
  loginCode: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" },
  ],
  loginPwd: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur" },
  ],
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
  userType: [{ required: true, message: "请选择用户类型", trigger: "change" }],
}

// 添加防抖函数
const debounce = (fn, delay) => {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 创建防抖版本的查询函数
const debouncedQuery = debounce(() => {
  queryParams.value.pageNum = 1
  getUserList()
}, 300) // 300ms 延迟

const handleQueryTextChange = (val) => {
  debouncedQuery()
}

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const res = await userApi.list4Table({
      pageNum: queryParams.value.pageNum,
      pageSize: queryParams.value.pageSize,
      queryText: queryParams.value.queryText || "",
    })
    if (res.msgResult === "success") {
      userList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error("获取用户列表失败:", error)
    ElMessage.error("获取用户列表失败")
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getUserList()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  getUserList()
}

// 添加页码改变处理函数
const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  getUserList()
}

// 新增用户
const handleAdd = () => {
  dialogType.value = "add"
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = "edit"
  userForm.value = { ...row }
  dialogVisible.value = true
}

// 重置密码
const handleResetPwd = (row) => {
  ElMessageBox.confirm("确定要重置该用户的密码吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const res = await userApi.resetPassword(row.userId)
      if (res.msgResult === "success") {
        ElMessage.success(res.msgInfo || "密码重置成功")
      }
    } catch (error) {
      console.error("重置密码失败:", error)
      ElMessage.error(error.response?.data?.msgInfo || "重置密码失败")
    }
  })
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该用户吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const res = await userApi.remove(row.userId)
      if (res.msgResult === "success") {
        ElMessage.success("删除成功")
        getUserList()
      } else {
        ElMessage.error(res.msgInfo || "删除失败")
      }
    } catch (error) {
      console.error("删除用户失败:", error)
      ElMessage.error(error.response?.data?.msgInfo || "删除失败")
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res =
          dialogType.value === "add"
            ? await userApi.add(userForm.value)
            : await userApi.edit(userForm.value)

        if (res.msgResult === "success") {
          ElMessage.success(
            dialogType.value === "add" ? "添加成功" : "修改成功"
          )
          dialogVisible.value = false
          getUserList()
        } else {
          ElMessage.error(
            res.msgInfo ||
              (dialogType.value === "add" ? "添加失败" : "修改失败")
          )
        }
      } catch (error) {
        console.error("提交失败:", error)
        ElMessage.error(error.response?.data?.msgInfo || "操作失败")
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
  userForm.value = {
    userId: undefined,
    loginCode: "",
    loginPwd: "",
    userName: "",
    userEmail: "",
    userPhone: "",
    userType: "user",
    roleType: "USER",
  }
}

// 监听用户类型变化，同步更新角色类型
watch(
  () => userForm.value.userType,
  (newType) => {
    userForm.value.roleType = newType.toUpperCase()
  }
)

onMounted(() => {
  getUserList()
})
</script>

<style scoped lang="scss">
.users-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left {
      display: flex;
      gap: 10px;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
