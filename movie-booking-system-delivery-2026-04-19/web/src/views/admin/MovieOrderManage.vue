<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card shadow="never">
        <div class="panel-head">
          <div>
            <div class="page-title">电影订单</div>
            <div class="page-subtitle">统一查看电影票订单、支付方式、锁座时效、票码和核销记录。</div>
          </div>
        </div>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="影院" prop="cinemaId"  v-if="currentUser.type==='ADMIN'">
            <el-select v-model="searchForm.cinemaId" placeholder="请选择" clearable filterable style="width: 150px">
              <el-option :label="item.nickname"  :value="item.id" :key="item.id" v-for="item in cinemaList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable filterable style="width: 150px">
              <el-option :label="item"  :value="item" :key="item" v-for="item in statusList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="searchForm.paymentMethod" placeholder="请选择" clearable filterable style="width: 150px">
              <el-option :label="item"  :value="item" :key="item" v-for="item in paymentMethodList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="票码" prop="ticketCode">
            <el-input v-model="searchForm.ticketCode" placeholder="输入票码搜索" clearable style="width: 170px" />
          </el-form-item>
          
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">批量删除</el-button>
        </el-space>
      </el-card>
      <el-card shadow="never">
        <el-alert
            title="后台订单页已同步支付方式、锁座倒计时、票码和核销时间，方便客服与影院直接核对订单"
            type="info"
            :closable="false"
            style="margin-bottom: 16px"
        />
        <el-table ref="tableComponents"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%"
                  @selection-change="selectionChange"
                  border>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="50"></el-table-column>
          <el-table-column label="电影信息" min-width="220">
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
          <el-table-column prop="startTime" label="开始时间" min-width="160">
            <template #default="scope">
              {{ formatDateTime(scope.row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间" min-width="160">
            <template #default="scope">
              {{ formatDateTime(scope.row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column label="订单金额" width="110">
            <template #default="scope">
              <span class="amount-text">￥{{ formatOrderAmount(scope.row.price) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="座位" width="100">
            <template #default="scope">
              {{ formatSeat(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="支付方式" width="110">
            <template #default="scope">
              <el-tag size="small" :type="getPaymentTagType(scope.row.paymentMethod)" effect="plain" round>
                {{ scope.row.paymentMethod || '--' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)" effect="plain" round>
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="票码" min-width="160">
            <template #default="scope">
              {{ scope.row.ticketCode || '--' }}
            </template>
          </el-table-column>
          <el-table-column label="锁座失效" min-width="150">
            <template #default="scope">
              {{ formatDateTime(scope.row.reserveExpireTime) }}
            </template>
          </el-table-column>
          <el-table-column label="核销时间" min-width="150">
            <template #default="scope">
              {{ formatDateTime(scope.row.verifyTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="用户名称" width="110"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column fixed="right" label="操作" width="120">
            <template #default="scope">
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">删除</el-button>
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
                  :total="pageInfo.total">
          </el-pagination>
        </div>
      </el-card>
    </el-space>

  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Delete, Refresh, Search} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import tools from "@/utils/tools.js";
import dayjs from "dayjs";
import {formatOrderAmount} from "@/utils/price.js";

const currentUser = ref(tools.getCurrentUser())

const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const pageInfo = ref({
  //当前页
  pageNum: 1,
  //分页大小
  pageSize: 10,
  //总条数
  total: 0
});
const searchForm = ref({
  cinemaId: undefined,
  screeningPlanId: undefined,
  status: undefined,
  paymentMethod: undefined,
  ticketCode: undefined,
  userId: undefined,
  
});

const  cinemaList=ref([])

getCinemaList()

function getCinemaList() {
  request.get("/cinema/list").then(res => {
    cinemaList.value = res.data;
  })
}

const  movieRoomList=ref([])

getMovieRoomList()

function getMovieRoomList() {
  request.get("/movieRoom/list").then(res => {
    movieRoomList.value = res.data;
  })
}

const  movieList=ref([])

getMovieList()

function getMovieList() {
  request.get("/movie/list").then(res => {
    movieList.value = res.data;
  })
}

const  statusList=ref(['待支付','待取票','已完成','已取消'])
const  paymentMethodList=ref(['支付宝','微信','银行卡'])



getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/movieOrder/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

/**
 * 选择分页
 * @param e
 */
function currentChange(e) {
  pageInfo.value.pageNum = e
  getPageList()
}

/**
 * 改变分页数量
 * @param e
 */
function sizeChange(e) {
  pageInfo.value.pageSize = e
  pageInfo.value.pageNum = 1
  getPageList()
}

/**
 * 搜索
 */
function search() {
  pageInfo.value.pageNum = 1
  getPageList()
}

/**
 * 重置搜索框
 */
function resetSearch() {
  searchFormComponents.value.resetFields();
  pageInfo.value.pageNum = 1
  getPageList()
}

const selectionRows = ref([]);

/**
 * 多选
 * @param rows
 */
function selectionChange(rows) {
  selectionRows.value = rows
}

/**
 * 单个删除
 * @param index
 * @param row
 */
function deleteOne(index, row) {
  batchDelete([row])
}

/**
 * 批量删除
 * @param rows
 */
function batchDelete(rows) {
  if (!rows) {
    rows = selectionRows.value;
  }
  let ids = rows.map(item => item.id);
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/movieOrder/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: "操作成功",
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除'
    });
    tableComponents.value.clearSelection();
  });
}

function formatDateTime(value) {
  if (!value) {
    return '--'
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm')
}

function formatSeat(row) {
  if (!row.numberOfX || !row.numberOfY) {
    return '--'
  }
  return `${row.numberOfX}排${row.numberOfY}座`
}

function getStatusTagType(status) {
  if (status === '已完成') return 'success'
  if (status === '待取票') return 'warning'
  if (status === '待支付') return 'info'
  if (status === '已取消') return 'danger'
  return 'info'
}

function getPaymentTagType(paymentMethod) {
  if (paymentMethod === '支付宝') return 'success'
  if (paymentMethod === '微信') return 'primary'
  if (paymentMethod === '银行卡') return 'warning'
  return 'info'
}
</script>

<style scoped>
.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
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

.movie-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.movie-thumb {
  width: 54px;
  height: 72px;
  border-radius: 8px;
  flex-shrink: 0;
}

.movie-meta {
  min-width: 0;
}

.movie-name {
  color: #303133;
  font-weight: 600;
  line-height: 1.4;
}

.movie-sub {
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}

.amount-text {
  color: #ef4238;
  font-weight: 600;
}
</style>
