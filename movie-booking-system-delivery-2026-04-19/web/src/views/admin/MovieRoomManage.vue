<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <el-card shadow="never">
        <div class="panel-head">
          <div>
            <div class="page-title">影厅管理</div>
            <div class="page-subtitle">统一维护影厅归属、厅名与座位规格；影院账号仅能维护自己的影厅。</div>
          </div>
        </div>

        <el-form ref="searchFormComponents" :model="searchForm" inline style="margin-top: 16px">
          <el-form-item v-if="currentUser.type === 'ADMIN'" label="电影院" prop="cinemaId">
            <el-select v-model="searchForm.cinemaId" clearable filterable placeholder="全部影院" style="width: 180px">
              <el-option v-for="item in cinemaList" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="影厅名称" prop="name">
            <el-input v-model="searchForm.name" clearable placeholder="搜索影厅名称" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>

        <el-space>
          <el-button type="primary" :icon="Plus" @click="add">新增影厅</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete()" :disabled="selectionRows.length <= 0">批量删除</el-button>
        </el-space>
      </el-card>

      <el-card shadow="never">
        <el-alert
          :title="currentUser.type === 'ADMIN' ? '管理员可维护所有影院影厅；影院账号进入本页时只显示自己名下影厅。' : '当前仅展示你所属影院的影厅信息。'"
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        />

        <el-table
          ref="tableComponents"
          :data="listData"
          border
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="selectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column v-if="currentUser.type === 'ADMIN'" prop="cinemaName" label="电影院" min-width="160" show-overflow-tooltip />
          <el-table-column prop="name" label="影厅名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="label" label="影厅标签" min-width="220" show-overflow-tooltip />
          <el-table-column prop="numberOfX" label="座位排数" width="100" />
          <el-table-column prop="numberOfY" label="每排座位数" width="120" />
          <el-table-column prop="createTime" label="创建时间" min-width="170" />
          <el-table-column fixed="right" label="操作" width="180">
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

    <el-dialog v-model="dialogOpen" :title="formData.id ? '编辑影厅' : '新增影厅'" width="620px">
      <el-form ref="formRef" :model="formData" label-width="110px">
        <el-form-item
          v-if="currentUser.type === 'ADMIN'"
          label="所属电影院"
          prop="cinemaId"
          :rules="[{ required: true, message: '请选择所属电影院', trigger: ['blur', 'change'] }]"
        >
          <el-select v-model="formData.cinemaId" clearable filterable placeholder="请选择电影院" style="width: 100%">
            <el-option v-for="item in cinemaList" :key="item.id" :label="item.nickname" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="影厅名称" prop="name" :rules="[{ required: true, message: '请输入影厅名称', trigger: ['blur', 'change'] }]">
          <el-input v-model="formData.name" placeholder="例如：1号激光厅" />
        </el-form-item>
        <el-form-item label="影厅标签" prop="label">
          <el-input v-model="formData.label" type="textarea" :rows="4" maxlength="100" show-word-limit placeholder="多个标签使用英文逗号分隔" />
        </el-form-item>
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="座位排数" prop="numberOfX" :rules="[{ required: true, message: '请输入座位排数', trigger: ['blur', 'change'] }]">
              <el-input-number v-model="formData.numberOfX" :min="1" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每排座位数" prop="numberOfY" :rules="[{ required: true, message: '请输入每排座位数', trigger: ['blur', 'change'] }]">
              <el-input-number v-model="formData.numberOfY" :min="1" :step="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" :icon="Check" @click="submit">提交</el-button>
        <el-button :icon="Close" @click="closeDialog">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Plus, Refresh, Search} from '@element-plus/icons-vue';
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import tools from "@/utils/tools.js";

const currentUser = ref(tools.getCurrentUser());
const searchFormComponents = ref();
const tableComponents = ref();
const formRef = ref();
const listData = ref([]);
const selectionRows = ref([]);
const dialogOpen = ref(false);
const formData = ref({});
const cinemaList = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  name: undefined,
  cinemaId: undefined
});

getCinemaList();
getPageList();

function getCinemaList() {
  request.get('/cinema/list').then((res) => {
    cinemaList.value = res.data || [];
  });
}

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/movieRoom/page', {params: data}).then((res) => {
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

function add() {
  formData.value = currentUser.value.type === 'ADMIN' ? {} : {cinemaId: currentUser.value.id};
  dialogOpen.value = true;
}

function edit(row) {
  formData.value = Object.assign({}, row);
  dialogOpen.value = true;
}

function closeDialog() {
  dialogOpen.value = false;
}

function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning('请先完成表单校验');
      return;
    }
    const payload = Object.assign({}, formData.value);
    const api = payload.id ? request.put('/movieRoom/update', payload) : request.post('/movieRoom/add', payload);
    api.then(() => {
      ElMessage.success(payload.id ? '更新成功' : '新增成功');
      dialogOpen.value = false;
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

function batchDelete(rows = selectionRows.value) {
  const ids = rows.map(item => item.id);
  if (!ids.length) {
    return;
  }
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete('/movieRoom/delBatch', {data: ids}).then(() => {
      ElMessage.success('操作成功');
      getPageList();
    });
  }).catch(() => {
    tableComponents.value?.clearSelection();
  });
}
</script>

<style scoped>
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.page-subtitle {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}
</style>
