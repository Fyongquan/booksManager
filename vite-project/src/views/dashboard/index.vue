<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总图书数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ statistics.totalBooks }}</h2>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ statistics.totalUsers }}</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="recent-books" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>最近添加的图书</span>
        </div>
      </template>
      <el-table :data="recentBooks" stripe style="width: 100%">
        <el-table-column prop="bookName" label="书名" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="publisher" label="出版社" />
        <el-table-column prop="gmtCreate" label="添加时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { bookApi } from "@/api/book"
import { userApi } from "@/api/user"

const statistics = ref({
  totalBooks: 0,
  totalUsers: 0,
})

const recentBooks = ref([])

// 获取统计数据
const getStatistics = async () => {
  try {
    // 获取图书总数
    const bookRes = await bookApi.getPageList({
      pageNum: 1,
      pageSize: 1,
    })
    if (bookRes.msgResult === "success") {
      statistics.value.totalBooks = bookRes.data.total
    }

    // 获取用户总数
    const userRes = await userApi.getUserCount()
    if (userRes.msgResult === "success") {
      statistics.value.totalUsers = userRes.data
    }
  } catch (error) {
    console.error("获取统计数据失败:", error)
  }
}

// 获取最近图书
const getRecentBooks = async () => {
  try {
    const res = await bookApi.getPageList({
      pageNum: 1,
      pageSize: 5,
      orderBy: "gmtCreate desc", // 按创建时间倒序
    })
    if (res.msgResult === "success") {
      console.log(res.data.list)
      recentBooks.value = res.data.list
    }
  } catch (error) {
    console.error("获取最近图书失败:", error)
  }
}

onMounted(() => {
  getStatistics()
  getRecentBooks()
})
</script>

<style scoped lang="scss">
.dashboard-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-body {
    text-align: center;
    h2 {
      margin: 0;
      font-size: 24px;
      color: #409eff;
    }
  }
}
</style>
