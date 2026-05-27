<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="电影名称" prop="movieName">
            <el-input v-model="searchForm.movieName" clearable></el-input>
          </el-form-item>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="searchForm.username" clearable></el-input>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length <= 0">批量删除</el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-alert title="浏览历史页已按后台记录页统一：突出用户、电影和浏览时间，方便排查热门片单与用户行为。" type="info" :closable="false" style="margin-bottom: 16px" />
        <el-table ref="tableComponents" :data="listData" tooltip-effect="dark" style="width: 100%" @selection-change="selectionChange" border>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="60"></el-table-column>
          <el-table-column label="电影信息" min-width="220">
            <template #default="scope">
              <div class="record-title">{{ scope.row.movieName || '--' }}</div>
              <div class="record-sub">行为类型：浏览详情</div>
            </template>
          </el-table-column>
          <el-table-column label="用户信息" width="180">
            <template #default="scope">
              <el-tag type="info" effect="plain" round>{{ scope.row.username || '--' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="浏览时间" min-width="160"></el-table-column>
          <el-table-column fixed="right" label="操作" width="120">
            <template #default="scope">
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px">
          <el-pagination @current-change="currentChange" @size-change="sizeChange" :page-size="pageInfo.pageSize" :current-page="pageInfo.pageNum" background layout="total,sizes, prev, pager, next" :total="pageInfo.total"></el-pagination>
        </div>
      </el-card>
    </el-space>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import { Delete, Refresh, Search } from '@element-plus/icons-vue'
import { ref, toRaw } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const pageInfo = ref({ pageNum: 1, pageSize: 10, total: 0 });
const searchForm = ref({ movieId: undefined, movieName: undefined, userId: undefined, username: undefined });

getPageList()
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/movieBrowsingHistory/page", { params: data }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}
function currentChange(e) { pageInfo.value.pageNum = e; getPageList() }
function sizeChange(e) { pageInfo.value.pageSize = e; getPageList() }
function search() { pageInfo.value.pageNum = 1; getPageList() }
function resetSearch() { searchFormComponents.value.resetFields(); pageInfo.value.pageNum = 1; getPageList() }

const selectionRows = ref([]);
function selectionChange(rows) { selectionRows.value = rows }
function deleteOne(index, row) { batchDelete([row]) }
function batchDelete(rows) {
  if (!rows) rows = selectionRows.value;
  let ids = rows.map(item => item.id);
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', center: true }).then(() => {
    request.delete("/movieBrowsingHistory/delBatch", { data: ids }).then(res => {
      if (!res) return
      ElMessage({ message: "操作成功", type: 'success' });
      getPageList()
    })
  }).catch(() => {
    ElMessage({ type: 'info', message: '已取消删除' });
    tableComponents.value.clearSelection();
  });
}
</script>

<style scoped>
.record-title { font-size: 14px; font-weight: 600; color: #1f2937; }
.record-sub { margin-top: 6px; font-size: 12px; color: #909399; }
</style>
