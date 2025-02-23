<template>
  <div class="borrow-books-container">
    <el-card>
      <template #header>
        <div class="header">
          <div class="left">
            <el-input
              v-model="queryParams.queryText"
              placeholder="请输入书名"
              style="width: 200px"
              clearable
              @input="handleQueryTextChange"
              @clear="handleQuery"
            />
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>搜索
            </el-button>
          </div>
          <el-button type="primary" @click="handleBorrow">
            <el-icon><Plus /></el-icon>借阅图书
          </el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="borrowList" stripe border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column
          prop="bookName"
          label="书名"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column prop="borrowTime" label="借阅时间" width="160">
          <template #default="{ row }">
            {{ row.borrowTime?.substring(0, 19) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnTime" label="应还时间" width="160">
          <template #default="{ row }">
            {{ row.returnTime?.substring(0, 19) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="actualReturnTime"
          label="实际归还时间"
          width="160"
        >
          <template #default="{ row }">
            {{ row.actualReturnTime?.substring(0, 19) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getBorrowStatusType(row.status)">
              {{ getBorrowStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'BORROWING'"
              type="primary"
              link
              @click="handleReturn(row)"
            >
              归还
            </el-button>
            <el-button
              v-if="row.status === 'BORROWING'"
              type="primary"
              link
              @click="handleRenew(row)"
            >
              续借
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.currentPage"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 借阅表单对话框 -->
    <el-dialog
      title="借阅图书"
      v-model="dialogVisible"
      width="500px"
      @close="resetForm"
      @open="loadBooks"
    >
      <el-form
        ref="borrowFormRef"
        :model="borrowForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="图书" prop="bookId">
          <el-select
            v-model="borrowForm.bookId"
            filterable
            placeholder="请选择图书"
            style="width: 100%"
          >
            <el-option
              v-for="item in bookOptions"
              :key="item.bookId"
              :label="getBookLabel(item)"
              :value="item.bookId"
              :disabled="item.stock <= 0"
            >
              <div
                style="
                  display: flex;
                  justify-content: space-between;
                  align-items: center;
                "
              >
                <span>{{ item.bookName }}</span>
                <span
                  :style="{
                    color: item.stock > 0 ? '#8492a6' : '#ff4949',
                    fontSize: '13px',
                  }"
                >
                  库存: {{ item.stock }}
                </span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-input-number v-model="borrowForm.days" :min="1" :max="30" />
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
import { ref, onMounted } from "vue"
import { Search, Plus } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { borrowApi } from "@/api/borrow"
import { bookApi } from "@/api/book"

// 查询参数
const queryParams = ref({
  queryText: "",
  currentPage: 1,
  pageSize: 10,
})

// 借阅列表数据
const borrowList = ref([])
const total = ref(0)
const loading = ref(false)

// 借阅表单相关
const dialogVisible = ref(false)
const borrowFormRef = ref(null)
const borrowForm = ref({
  bookId: null,
  days: 7,
})
const submitLoading = ref(false)

// 图书选择相关
const bookOptions = ref([])
const bookLoading = ref(false)

// 表单校验规则
const rules = {
  bookId: [{ required: true, message: "请选择图书", trigger: "change" }],
  days: [{ required: true, message: "请输入借阅天数", trigger: "blur" }],
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
  getBorrowList()
}, 300) // 300ms 延迟

const handleQueryTextChange = (val) => {
  debouncedQuery()
}

// 获取借阅列表
const getBorrowList = async () => {
  loading.value = true
  try {
    const res = await borrowApi.getMyBorrows({
      queryText: queryParams.value.queryText,
      currentPage: queryParams.value.currentPage,
      pageSize: queryParams.value.pageSize,
    })
    if (res.msgResult === "success") {
      borrowList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error("获取借阅列表失败:", error)
  } finally {
    loading.value = false
  }
}

// 加载图书列表
const loadBooks = async () => {
  bookLoading.value = true
  try {
    const res = await bookApi.getPageList({
      currentPage: 1,
      pageSize: 1000,
      queryText: "",
    })
    if (res.msgResult === "success") {
      // 显示所有图书，但库存为0的会被禁用
      bookOptions.value = res.data.list
    }
  } catch (error) {
    console.error("加载图书列表失败:", error)
    ElMessage.error("加载图书列表失败")
  } finally {
    bookLoading.value = false
  }
}

// 获取图书选项的显示文本
const getBookLabel = (book) => {
  return `${book.bookName} - ${book.author}`
}

// 借阅状态相关
const getBorrowStatusType = (status) => {
  const map = {
    BORROWING: "primary",
    RETURNED: "success",
    OVERDUE: "danger",
    OVERDUE_RETURNED: "warning",
  }
  return map[status]
}

const getBorrowStatusText = (status) => {
  const map = {
    BORROWING: "借阅中",
    RETURNED: "按时归还",
    OVERDUE: "已逾期",
    OVERDUE_RETURNED: "逾期归还",
  }
  return map[status]
}

// 搜索相关
const handleQuery = () => {
  queryParams.value.currentPage = 1
  getBorrowList()
}

// 分页相关
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  getBorrowList()
}

const handleCurrentChange = (val) => {
  queryParams.value.currentPage = val
  getBorrowList()
}

// 借阅相关
const handleBorrow = () => {
  dialogVisible.value = true
  // 对话框打开时会触发 loadBooks
}

const resetForm = () => {
  if (borrowFormRef.value) {
    borrowFormRef.value.resetFields()
  }
  borrowForm.value = {
    bookId: null,
    days: 7,
  }
}

const handleSubmit = async () => {
  if (!borrowFormRef.value) return

  await borrowFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res = await borrowApi.borrow(borrowForm.value)
        if (res.msgResult === "success") {
          ElMessage.success("借阅成功")
          dialogVisible.value = false
          getBorrowList()
          // 更新图书列表以反映最新的库存状态
          loadBooks()
        }
      } catch (error) {
        console.error("借阅失败:", error)
        ElMessage.error(error.response?.data?.msgInfo || "借阅失败")
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 归还图书
const handleReturn = async (row) => {
  try {
    let message = "确认归还该图书?"
    let type = "warning"

    // 检查是否逾期
    if (new Date() > new Date(row.returnTime)) {
      message = "该图书已逾期，是否确认归还?"
      type = "error"
    }

    await ElMessageBox.confirm(message, "提示", {
      type,
      confirmButtonText: "确认归还",
      cancelButtonText: "取消",
    })

    const res = await borrowApi.return(row.borrowId)
    if (res.msgResult === "success") {
      ElMessage.success("归还成功")
      getBorrowList()
      loadBooks()
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("归还失败:", error)
      ElMessage.error(error.response?.data?.msgInfo || "归还失败")
    }
  }
}

// 续借图书
const handleRenew = async (row) => {
  try {
    await ElMessageBox.confirm("确认续借该图书?", "提示", {
      type: "warning",
    })
    const res = await borrowApi.renew(row.borrowId)
    if (res.msgResult === "success") {
      ElMessage.success("续借成功")
      getBorrowList()
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("续借失败:", error)
    }
  }
}

onMounted(() => {
  getBorrowList()
})
</script>

<style scoped lang="scss">
.borrow-books-container {
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
