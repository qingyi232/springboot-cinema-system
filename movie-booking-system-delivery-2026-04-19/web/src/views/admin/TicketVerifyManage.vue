<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <div class="verify-header">
          <div>
            <div class="page-title">验票核销</div>
            <div class="page-subtitle">支持影院或管理员按票码核销，核销后订单自动变为已完成并累计票房</div>
          </div>
        </div>

        <el-alert
          :title="currentUser?.type === 'CINEMA' ? '当前仅展示本影院可核销订单' : '管理员可查看并核销全部影院订单'"
          type="info"
          :closable="false"
          style="margin: 16px 0"
        />

        <div class="quick-verify-box">
          <el-input v-model="ticketCode" placeholder="请输入票码，例如 TKXXXXXXXXXXXXXX" clearable @keyup.enter="quickVerify" />
          <el-button type="primary" :icon="Checked" :loading="verifyLoading" @click="quickVerify">立即核销</el-button>
        </div>

        <el-form ref="searchFormComponents" :model="searchForm" inline style="margin-top: 16px">
          <el-form-item label="票码" prop="ticketCode">
            <el-input v-model="searchForm.ticketCode" clearable placeholder="按票码筛选" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="searchForm.status" clearable placeholder="请选择" style="width: 140px">
              <el-option label="待取票" value="待取票" />
              <el-option label="已完成" value="已完成" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card>
        <el-table :data="listData" border style="width: 100%">
          <el-table-column prop="id" label="订单ID" width="90" />
          <el-table-column label="电影信息" min-width="250">
            <template #default="scope">
              <div class="movie-cell">
                <el-image
                  v-if="scope.row.movieMainImg"
                  class="movie-thumb"
                  :src="scope.row.movieMainImg"
                  :preview-src-list="[scope.row.movieMainImg]"
                  :preview-teleported="true"
                />
                <div class="movie-meta">
                  <div class="movie-name">{{ scope.row.movieName || '--' }}</div>
                  <div class="movie-sub">{{ scope.row.cinemaName || '--' }} · {{ scope.row.movieRoomName || '--' }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="开场时间" min-width="160">
            <template #default="scope">{{ formatDateTime(scope.row.startTime) }}</template>
          </el-table-column>
          <el-table-column label="座位" width="100">
            <template #default="scope">{{ formatSeat(scope.row) }}</template>
          </el-table-column>
          <el-table-column prop="ticketCode" label="票码" width="190" />
          <el-table-column prop="userName" label="用户" width="110" />
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已完成' ? 'success' : 'warning'" effect="plain" round>
                {{ scope.row.status || '--' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="核销时间" min-width="160">
            <template #default="scope">{{ formatDateTime(scope.row.verifyTime) }}</template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="130">
            <template #default="scope">
              <el-button
                type="success"
                :icon="Checked"
                :disabled="scope.row.status !== '待取票'"
                @click="verifyRow(scope.row)"
              >
                核销
              </el-button>
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
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Checked, Refresh, Search} from '@element-plus/icons-vue';
import {ref, toRaw} from "vue";
import {ElMessage} from "element-plus";
import tools from "@/utils/tools.js";
import dayjs from "dayjs";

const currentUser = ref(tools.getCurrentUser());
const searchFormComponents = ref();
const listData = ref([]);
const verifyLoading = ref(false);
const ticketCode = ref('');
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  ticketCode: undefined,
  status: '待取票'
});

getPageList();

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/movieOrder/page', {params: data}).then((res) => {
    listData.value = res.data.list || [];
    pageInfo.value.total = res.data.total || 0;
  });
}

function search() {
  pageInfo.value.pageNum = 1;
  getPageList();
}

function resetSearch() {
  searchFormComponents.value?.resetFields();
  searchForm.value.status = '待取票';
  pageInfo.value.pageNum = 1;
  getPageList();
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

function quickVerify() {
  if (!ticketCode.value) {
    ElMessage.warning('请输入票码');
    return;
  }
  verifyLoading.value = true;
  request.post('/movieOrder/verifyByCode', {ticketCode: ticketCode.value}).then(() => {
    ElMessage.success('核销成功');
    ticketCode.value = '';
    getPageList();
  }).finally(() => {
    verifyLoading.value = false;
  });
}

function verifyRow(row) {
  request.post('/movieOrder/verifyByCode', {ticketCode: row.ticketCode}).then(() => {
    ElMessage.success('核销成功');
    getPageList();
  });
}

function formatDateTime(value) {
  if (!value) {
    return '--';
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
}

function formatSeat(row) {
  if (!row.numberOfX || !row.numberOfY) {
    return '--';
  }
  return `${row.numberOfX}排${row.numberOfY}座`;
}
</script>

<style scoped>
.verify-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.quick-verify-box {
  display: flex;
  gap: 12px;
  align-items: center;
}

.movie-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.movie-thumb {
  width: 52px;
  height: 72px;
  border-radius: 10px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.movie-name {
  font-weight: 600;
  color: #303133;
}

.movie-sub {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}
</style>
