<template>
  <div class="books-container">
    <el-card>
      <template #header>
        <div class="header">
          <div class="left">
            <el-input
              v-model="queryParams.queryText"
              placeholder="请输入书名或作者"
              style="width: 200px"
              clearable
              @input="handleQueryTextChange"
              @clear="handleQuery"
            />
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>搜索
            </el-button>
          </div>
          <div class="right">
            <!-- 导入导出按钮 -->
            <el-upload
              class="excel-upload"
              :http-request="customUpload"
              :show-file-list="false"
              accept=".xlsx,.xls"
              :before-upload="beforeExcelUpload"
              :on-success="handleExcelSuccess"
              :on-error="handleExcelError"
            >
              <el-button type="success">
                <el-icon><Upload /></el-icon>导入Excel
              </el-button>
            </el-upload>

            <el-button type="warning" @click="handleExport">
              <el-icon><Download /></el-icon>导出Excel
            </el-button>

            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>新增图书
            </el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="bookList" stripe border>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column
          prop="bookName"
          label="书名"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column prop="author" label="作者" min-width="100" />
        <el-table-column
          prop="publisher"
          label="出版社"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="isbn"
          label="ISBN"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }"> ¥{{ row.price.toFixed(2) }} </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="publishDate" label="出版日期" width="100" />
        <el-table-column
          prop="description"
          label="描述"
          min-width="200"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row.description || "暂无描述" }}
          </template>
        </el-table-column>
        <el-table-column
          prop="gmtCreate"
          label="创建时间"
          width="160"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row.gmtCreate?.substring(0, 19) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="gmtModified"
          label="修改时间"
          width="160"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ row.gmtModified?.substring(0, 19) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              编辑
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
          :pager-count="7"
          prev-text="上一页"
          next-text="下一页"
        >
          <template #total>
            共 <b>{{ total }}</b> 条
          </template>
          <template #sizes="{ size }">
            <el-space>
              每页
              <el-select
                v-model="queryParams.pageSize"
                @change="handleSizeChange"
              >
                <el-option
                  v-for="item in [10, 20, 30, 50]"
                  :key="item"
                  :label="`${item}条/页`"
                  :value="item"
                />
              </el-select>
            </el-space>
          </template>
          <template #jumper>
            前往
            <el-input
              v-model="queryParams.pageNum"
              @change="handleCurrentChange"
              class="jumper-input"
            />
            页
          </template>
        </el-pagination>
      </div>
    </el-card>

    <!-- 图书表单对话框 -->
    <el-dialog
      :title="dialogType === 'add' ? '新增图书' : '编辑图书'"
      v-model="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="bookFormRef"
        :model="bookForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="书名" prop="bookName">
          <el-input v-model="bookForm.bookName" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="bookForm.price"
            :precision="2"
            :step="0.1"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="bookForm.stock" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="bookForm.category" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="bookForm.publishDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" />
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
import { ref, computed, onMounted } from "vue"
import { Search, Plus, Upload, Download } from "@element-plus/icons-vue"
import { ElMessage, ElMessageBox } from "element-plus"
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

// 查询参数
const queryParams = ref({
  queryText: "",
  pageNum: 1,
  pageSize: 10,
})

// 创建防抖版本的查询函数
const debouncedQuery = debounce(() => {
  queryParams.value.pageNum = 1
  getBookList()
}, 300) // 300ms 延迟

// 监听输入变化
const handleQueryTextChange = (val) => {
  debouncedQuery()
}

const loading = ref(false)
const bookList = ref([])
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref("add")
const bookFormRef = ref(null)
const submitLoading = ref(false)

const bookForm = ref({
  bookId: undefined,
  bookName: "",
  author: "",
  publisher: "",
  isbn: "",
  price: 0,
  stock: 0,
  category: "",
  publishDate: "",
  description: "",
})

// 表单验证规则
const rules = {
  bookName: [{ required: true, message: "请输入书名", trigger: "blur" }],
  author: [{ required: true, message: "请输入作者", trigger: "blur" }],
  publisher: [{ required: true, message: "请输入出版社", trigger: "blur" }],
  isbn: [{ required: true, message: "请输入ISBN", trigger: "blur" }],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
  stock: [{ required: true, message: "请输入库存", trigger: "blur" }],
  category: [{ required: true, message: "请输入分类", trigger: "blur" }],
  publishDate: [{ required: true, message: "请选择出版日期", trigger: "blur" }],
}

// 获取图书列表
const getBookList = async () => {
  loading.value = true
  try {
    const res = await bookApi.getPageList({
      queryText: queryParams.value.queryText,
      pageNum: queryParams.value.pageNum,
      pageSize: queryParams.value.pageSize,
    })
    if (res.msgResult === "success") {
      bookList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error("获取图书列表失败:", error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getBookList()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.value.pageSize = val
  getBookList()
}

const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val
  getBookList()
}

// 新增图书
const handleAdd = () => {
  dialogType.value = "add"
  dialogVisible.value = true
}

// 编辑图书
const handleEdit = (row) => {
  dialogType.value = "edit"
  bookForm.value = { ...row }
  dialogVisible.value = true
}

// 删除图书
const handleDelete = (row) => {
  ElMessageBox.confirm("确定要删除该图书吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      const res = await bookApi.delete(row.bookId)
      if (res.msgResult === "success") {
        ElMessage.success("删除成功")
        getBookList()
      }
    } catch (error) {
      console.error("删除图书失败:", error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!bookFormRef.value) return

  await bookFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = dialogType.value === "add" ? bookApi.add : bookApi.update
        const res = await api(bookForm.value)
        if (res.msgResult === "success") {
          ElMessage.success(
            dialogType.value === "add" ? "添加成功" : "修改成功"
          )
          dialogVisible.value = false
          getBookList()
        }
      } catch (error) {
        console.error("提交失败:", error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (bookFormRef.value) {
    bookFormRef.value.resetFields()
  }
  bookForm.value = {
    bookId: undefined,
    bookName: "",
    author: "",
    publisher: "",
    isbn: "",
    price: 0,
    stock: 0,
    category: "",
    publishDate: "",
    description: "",
  }
}

// 自定义上传方法
const customUpload = async (options) => {
  try {
    const res = await bookApi.importExcel(options.file)
    handleExcelSuccess(res)
  } catch (error) {
    handleExcelError(error)
  }
}

// Excel导入前的验证
const beforeExcelUpload = (file) => {
  const isExcel = /\.(xlsx|xls)$/.test(file.name)
  if (!isExcel) {
    ElMessage.error("只能上传Excel文件!")
    return false
  }
  return true
}

// Excel导入成功
const handleExcelSuccess = (res) => {
  if (res.msgResult === "success") {
    ElMessage.success("导入成功")
    // 刷新列表
    getBookList()
  } else {
    ElMessage.error(res.msgInfo || "导入失败")
  }
}

// Excel导入失败
const handleExcelError = (err) => {
  console.error("导入失败:", err)
  ElMessage.error("导入失败")
}

// 导出Excel
const handleExport = async () => {
  try {
    const response = await bookApi.exportExcel()
    const blob = new Blob([response], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", "图书列表.xlsx")
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error("导出失败:", error)
    ElMessage.error("导出失败")
  }
}

onMounted(() => {
  getBookList()
})
</script>

<style scoped lang="scss">
.books-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left {
      display: flex;
      gap: 10px;
    }

    .right {
      display: flex;
      gap: 10px;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;

    .jumper-input {
      width: 50px;
      margin: 0 4px;
    }

    :deep(.el-pagination) {
      .el-pagination__total {
        margin-right: 16px;
      }
    }
  }
}

.excel-upload {
  display: inline-block;
}
</style>
