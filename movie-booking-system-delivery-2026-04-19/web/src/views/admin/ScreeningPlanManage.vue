<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <el-card shadow="never">
        <div class="panel-head">
          <div>
            <div class="page-title">放映计划管理</div>
            <div class="page-subtitle">统一维护影厅、电影、场次与单张票价，前台购票链路会实时使用这里的排片数据。</div>
          </div>
        </div>

        <el-form ref="searchFormComponents" :model="searchForm" inline style="margin-top: 16px">
          <el-form-item v-if="currentUser.type === 'ADMIN'" label="电影院" prop="cinemaId">
            <el-select v-model="searchForm.cinemaId" clearable filterable placeholder="全部影院" style="width: 180px" @change="onSearchCinemaChange">
              <el-option v-for="item in cinemaList" :key="item.id" :label="item.nickname" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="影厅" prop="movieRoomId">
            <el-select v-model="searchForm.movieRoomId" clearable filterable placeholder="全部影厅" style="width: 180px">
              <el-option v-for="item in searchMovieRoomOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="电影" prop="movieId">
            <el-select v-model="searchForm.movieId" clearable filterable placeholder="全部电影" style="width: 180px">
              <el-option v-for="item in movieList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>

        <el-space>
          <el-button type="primary" :icon="Plus" @click="add">新增排片</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete()" :disabled="selectionRows.length <= 0">批量删除</el-button>
        </el-space>
      </el-card>

      <el-card shadow="never">
        <el-alert
          :title="currentUser.type === 'ADMIN' ? '管理员可维护全部影院排片；影院账号进入本页时仅展示自己影院的排片。' : '当前仅展示你所属影院的排片信息。'"
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
          <el-table-column v-if="currentUser.type === 'ADMIN'" prop="cinemaName" label="电影院" min-width="150" show-overflow-tooltip />
          <el-table-column prop="movieRoomName" label="影厅" min-width="140" show-overflow-tooltip />
          <el-table-column prop="movieName" label="电影" min-width="180" show-overflow-tooltip />
          <el-table-column label="开始时间" min-width="170">
            <template #default="scope">{{ formatDateTime(scope.row.startTime) }}</template>
          </el-table-column>
          <el-table-column label="结束时间" min-width="170">
            <template #default="scope">{{ formatDateTime(scope.row.endTime) }}</template>
          </el-table-column>
          <el-table-column label="排片状态" width="110">
            <template #default="scope">
              <el-tag :type="getPlanStatus(scope.row).type" effect="plain" round>{{ getPlanStatus(scope.row).label }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="单张票价" width="120">
            <template #default="scope"><span class="price-text">{{ formatSingleTicketPrice(scope.row.price, { fallback: '待设置' }) }}</span></template>
          </el-table-column>
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

    <el-dialog v-model="dialogOpen" :title="formData.id ? '编辑排片' : '新增排片'" width="640px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item
          v-if="currentUser.type === 'ADMIN'"
          label="电影院"
          prop="cinemaId"
          :rules="[{ required: true, message: '请选择电影院', trigger: ['blur', 'change'] }]"
        >
          <el-select v-model="formData.cinemaId" clearable filterable placeholder="请选择电影院" style="width: 100%" @change="onFormCinemaChange">
            <el-option v-for="item in cinemaList" :key="item.id" :label="item.nickname" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="影厅" prop="movieRoomId" :rules="[{ required: true, message: '请选择影厅', trigger: ['blur', 'change'] }]">
          <el-select v-model="formData.movieRoomId" clearable filterable placeholder="请选择影厅" style="width: 100%">
            <el-option v-for="item in formMovieRoomOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="电影" prop="movieId" :rules="[{ required: true, message: '请选择电影', trigger: ['blur', 'change'] }]">
          <el-select v-model="formData.movieId" clearable filterable placeholder="请选择电影" style="width: 100%">
            <el-option v-for="item in movieList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime" :rules="[{ required: true, message: '请选择开始时间', trigger: ['blur', 'change'] }]">
              <el-date-picker v-model="formData.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" :shortcuts="shortcuts" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime" :rules="[{ required: true, message: '请选择结束时间', trigger: ['blur', 'change'] }]">
              <el-date-picker v-model="formData.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" :shortcuts="shortcuts" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="票价" prop="price" :rules="[{ required: true, message: '请输入票价', trigger: ['blur', 'change'] }]">
          <el-input-number v-model="formData.price" :min="0.01" :precision="2" :step="1" controls-position="right" style="width: 100%" />
        </el-form-item>
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
import {computed, ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import tools from "@/utils/tools.js";
import dayjs from "dayjs";
import {formatSingleTicketPrice} from "@/utils/price.js";

const currentUser = ref(tools.getCurrentUser());
const searchFormComponents = ref();
const tableComponents = ref();
const formRef = ref();
const listData = ref([]);
const selectionRows = ref([]);
const dialogOpen = ref(false);
const formData = ref({});
const cinemaList = ref([]);
const movieRoomList = ref([]);
const movieList = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  cinemaId: undefined,
  movieRoomId: undefined,
  movieId: undefined
});

const shortcuts = [
  { text: '今天', value: new Date() },
  { text: '明天', value: () => dayjs().add(1, 'day').toDate() },
  { text: '三天后', value: () => dayjs().add(3, 'day').toDate() },
  { text: '一周后', value: () => dayjs().add(7, 'day').toDate() }
];

const searchMovieRoomOptions = computed(() => {
  if (currentUser.value.type !== 'ADMIN' || !searchForm.value.cinemaId) {
    return movieRoomList.value;
  }
  return movieRoomList.value.filter(item => item.cinemaId === searchForm.value.cinemaId);
});

const formMovieRoomOptions = computed(() => {
  if (currentUser.value.type !== 'ADMIN' || !formData.value.cinemaId) {
    return movieRoomList.value;
  }
  return movieRoomList.value.filter(item => item.cinemaId === formData.value.cinemaId);
});

getCinemaList();
getMovieRoomList();
getMovieList();
getPageList();

function getCinemaList() {
  request.get('/cinema/list').then((res) => {
    cinemaList.value = res.data || [];
  });
}

function getMovieRoomList() {
  request.get('/movieRoom/list').then((res) => {
    movieRoomList.value = res.data || [];
  });
}

function getMovieList() {
  request.get('/movie/list').then((res) => {
    movieList.value = res.data || [];
  });
}

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/screeningPlan/page', {params: data}).then((res) => {
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

function onSearchCinemaChange() {
  if (!searchMovieRoomOptions.value.find(item => item.id === searchForm.value.movieRoomId)) {
    searchForm.value.movieRoomId = undefined;
  }
}

function onFormCinemaChange() {
  if (!formMovieRoomOptions.value.find(item => item.id === formData.value.movieRoomId)) {
    formData.value.movieRoomId = undefined;
  }
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
    const api = payload.id ? request.put('/screeningPlan/update', payload) : request.post('/screeningPlan/add', payload);
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
    request.delete('/screeningPlan/delBatch', {data: ids}).then(() => {
      ElMessage.success('操作成功');
      getPageList();
    });
  }).catch(() => {
    tableComponents.value?.clearSelection();
  });
}

function formatDateTime(value) {
  if (!value) {
    return '--';
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm');
}

function getPlanStatus(row) {
  const start = dayjs(row.startTime);
  const end = dayjs(row.endTime);
  const now = dayjs();
  if (!start.isValid() || !end.isValid()) {
    return {label: '待确认', type: 'info'};
  }
  if (now.isBefore(start)) {
    return {label: '待放映', type: 'warning'};
  }
  if (now.isAfter(end)) {
    return {label: '已散场', type: 'info'};
  }
  return {label: '放映中', type: 'success'};
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

.price-text {
  color: #ef4238;
  font-weight: 700;
}
</style>
