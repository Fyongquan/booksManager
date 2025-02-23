<template>
  <div class="borrow-container">
    <el-card>
      <template #header>
        <div class="header">
          <div class="left">
            <el-input
              v-model="queryParams.queryText"
              placeholder="请输入书名/借阅人"
              style="width: 200px"
              clearable
              @input="handleQueryTextChange"
              @clear="handleQuery"
            />
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>搜索
            </el-button>
          </div>
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
        <el-table-column prop="userName" label="借阅人" width="100" />
        <el-table-column prop="borrowTime" label="借阅时间" width="160">
          <template #default="{ row }">
            {{ row.borrowTime?.substring(0, 19) }}
          </template>
        </el-table-column>
        <el-table-column prop="returnTime" label="应还时间" width="160">
          <template #default="{ row }">
            <span
              :style="{
                color:
                  new Date(row.returnTime) < new Date() &&
                  row.status === 'BORROWING'
                    ? 'red'
                    : '',
              }"
            >
              {{ row.returnTime?.substring(0, 19) }}
            </span>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { Search, Plus } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { borrowApi } from "@/api/borrow"
import { bookApi } from "@/api/book"

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

// 查询参数
const queryParams = ref({
  queryText: "",
  currentPage: 1,
  pageSize: 10,
})

const loading = ref(false)
const borrowList = ref([])
const total = ref(0)

// 借阅状态
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

// 添加时间格式化显示
const formatDateTime = (time) => {
  if (!time) return ""
  const date = new Date(time)
  const now = new Date()

  // 如果是借阅中的记录，且已过还书时间，显示红色
  if (time < now && status === "BORROWING") {
    return `<span style="color: red">${time.substring(0, 19)}</span>`
  }
  return time.substring(0, 19)
}

// 获取借阅列表
const getBorrowList = async () => {
  loading.value = true
  try {
    const res = await borrowApi.getList({
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

onMounted(() => {
  getBorrowList()
})
</script>

<style scoped lang="scss">
.borrow-container {
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
