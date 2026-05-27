<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="用户名" prop="username">
            <el-input v-model="searchForm.username" clearable placeholder="搜索评价用户" />
          </el-form-item>
          <el-form-item label="电影名称" prop="movieName">
            <el-input v-model="searchForm.movieName" clearable placeholder="搜索影片名称" />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input v-model="searchForm.content" clearable placeholder="搜索评价内容" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length <= 0">
          批量删除
        </el-button>
      </el-card>

      <el-card>
        <el-alert
          title="评分口径已统一为 1~5 星：前台用户评价、影片详情展示和后台编辑全部保持一致。"
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        />
        <el-table
          ref="tableComponents"
          :data="listData"
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="selectionChange"
          border
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="评价对象" min-width="220">
            <template #default="scope">
              <div class="record-title">{{ scope.row.movieName || '--' }}</div>
              <div class="record-sub">评价用户：{{ scope.row.username || '--' }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="评价内容" min-width="260" show-overflow-tooltip />
          <el-table-column label="评分" width="150">
            <template #default="scope">
              <div class="rate-cell">
                <el-rate :model-value="Number(scope.row.rate || 0)" disabled />
                <span class="rate-text">{{ formatRate(scope.row.rate) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="160" />
          <el-table-column fixed="right" label="操作" width="200">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.row)">编辑</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px">
          <el-pagination
            @current-change="currentChange"
            @size-change="sizeChange"
            :page-size="pageInfo.pageSize"
            :current-page="pageInfo.pageNum"
            background
            layout="total,sizes, prev, pager, next"
            :total="pageInfo.total"
          />
        </div>
      </el-card>
    </el-space>

    <el-dialog v-model="dialogOpen" v-if="dialogOpen" title="编辑评价" width="560px">
      <el-form ref="formRef" :model="formData" label-width="90px">
        <el-form-item
          label="内容"
          prop="content"
          :rules="[{ required: true, message: '请输入评价内容', trigger: ['blur', 'change'] }]"
        >
          <el-input v-model="formData.content" type="textarea" :rows="5" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item
          label="评分"
          prop="rate"
          :rules="[{ required: true, message: '请选择评分', trigger: ['blur', 'change'] }]"
        >
          <el-rate v-model="formData.rate" show-score text-color="#ff9900" score-template="{value} 星" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">提交</el-button>
          <el-button @click="closeDialog" :icon="Close">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Search} from '@element-plus/icons-vue';
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const pageInfo = ref({pageNum: 1, pageSize: 10, total: 0});
const searchForm = ref({
  userId: undefined,
  username: undefined,
  movieId: undefined,
  movieName: undefined,
  movieOrderId: undefined,
  content: undefined
});
const dialogOpen = ref(false);
const formData = ref({});
const formRef = ref();
const selectionRows = ref([]);

getPageList();

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get("/movieOrderEvaluate/page", {params: data}).then(res => {
    listData.value = res.data.list || [];
    pageInfo.value.total = res.data.total || 0;
  });
}

function currentChange(pageNum) {
  pageInfo.value.pageNum = pageNum;
  getPageList();
}

function sizeChange(pageSize) {
  pageInfo.value.pageSize = pageSize;
  pageInfo.value.pageNum = 1;
  getPageList();
}

function search() {
  pageInfo.value.pageNum = 1;
  getPageList();
}

function resetSearch() {
  searchFormComponents.value?.resetFields();
  pageInfo.value.pageNum = 1;
  getPageList();
}

function formatRate(value) {
  const numericValue = Number(value);
  if (!Number.isFinite(numericValue) || numericValue <= 0) {
    return '--';
  }
  return `${numericValue.toFixed(1).replace(/\.0$/, '')} 星`;
}

function edit(row) {
  formData.value = {
    id: row.id,
    content: row.content,
    rate: Number(row.rate || 5)
  };
  dialogOpen.value = true;
}

function closeDialog() {
  dialogOpen.value = false;
}

function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning("验证失败，请检查表单");
      return;
    }
    request.put("/movieOrderEvaluate/update", formData.value).then(() => {
      dialogOpen.value = false;
      ElMessage.success("操作成功");
      getPageList();
    });
  });
}

function selectionChange(rows) {
  selectionRows.value = rows;
}

function deleteOne(row) {
  batchDelete([row]);
}

function batchDelete(rows) {
  const targetRows = rows || selectionRows.value;
  const ids = targetRows.map(item => item.id);
  if (!ids.length) {
    return;
  }
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/movieOrderEvaluate/delBatch", {data: ids}).then(() => {
      ElMessage.success("操作成功");
      getPageList();
    });
  }).catch(() => {
    tableComponents.value?.clearSelection();
  });
}
</script>

<style scoped>
.record-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.record-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}

.rate-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.rate-text {
  color: #606266;
  font-size: 12px;
}
</style>
