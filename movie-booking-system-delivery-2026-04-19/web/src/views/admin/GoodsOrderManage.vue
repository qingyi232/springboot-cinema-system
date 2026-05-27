<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="商品名称" prop="goodsName">
            <el-input v-model="searchForm.goodsName" clearable placeholder="搜索卖品名称" />
          </el-form-item>
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="searchForm.paymentMethod" clearable placeholder="全部" style="width: 140px">
              <el-option label="支付宝" value="支付宝" />
              <el-option label="微信" value="微信" />
              <el-option label="银行卡" value="银行卡" />
            </el-select>
          </el-form-item>
          <el-form-item label="评价状态" prop="hasEvaluate">
            <el-select v-model="searchForm.hasEvaluate" clearable placeholder="全部" style="width: 140px">
              <el-option label="已评价" value="1" />
              <el-option label="未评价" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="searchForm.status" clearable placeholder="全部" style="width: 140px">
              <el-option label="已完成" value="已完成" />
            </el-select>
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
          title="卖品订单已经和用户端评价联动：后台可以直接查看支付方式、金额、评分和评价内容。"
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
          <el-table-column label="商品信息" min-width="260">
            <template #default="scope">
              <div class="goods-cell">
                <el-image
                  v-if="scope.row.goodsMainImg"
                  class="goods-thumb"
                  :src="scope.row.goodsMainImg"
                  :preview-src-list="[scope.row.goodsMainImg]"
                  :preview-teleported="true"
                  fit="cover"
                />
                <div class="goods-meta">
                  <div class="goods-name">{{ scope.row.goodsName || '--' }}</div>
                  <div class="goods-sub">订单号：{{ scope.row.id }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="110">
            <template #default="scope">￥{{ formatPrice(scope.row.unitPrice) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="90" />
          <el-table-column label="总金额" width="120">
            <template #default="scope"><span class="amount-text">￥{{ formatPrice(scope.row.totalPrice) }}</span></template>
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
              <el-tag type="success" effect="plain" round>{{ scope.row.status || '已完成' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="用户名" width="110" />
          <el-table-column label="评价" min-width="240">
            <template #default="scope">
              <div v-if="scope.row.evaluateTime" class="evaluate-cell">
                <el-rate :model-value="Number(scope.row.evaluateRate || 0)" disabled />
                <div class="evaluate-text">{{ scope.row.evaluateContent || '好评' }}</div>
                <div class="goods-sub">评价时间：{{ scope.row.evaluateTime }}</div>
              </div>
              <span v-else class="goods-sub">暂未评价</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="170" />
          <el-table-column fixed="right" label="操作" width="120">
            <template #default="scope">
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
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Delete, Refresh, Search} from '@element-plus/icons-vue';
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {formatOrderAmount} from "@/utils/price.js";

const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const selectionRows = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  goodsName: undefined,
  paymentMethod: undefined,
  hasEvaluate: undefined,
  status: '已完成'
});

getPageList();

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/goodsOrder/page', {params: data}).then((res) => {
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
  searchForm.value.status = '已完成';
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
    request.delete('/goodsOrder/delBatch', {data: ids}).then(() => {
      ElMessage.success('操作成功');
      getPageList();
    });
  }).catch(() => {
    tableComponents.value?.clearSelection();
  });
}

function getPaymentTagType(paymentMethod) {
  if (paymentMethod === '支付宝') return 'success';
  if (paymentMethod === '微信') return 'primary';
  if (paymentMethod === '银行卡') return 'warning';
  return 'info';
}

function formatPrice(value) {
  return formatOrderAmount(value, {digits: 2});
}
</script>

<style scoped>
.goods-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-thumb {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.goods-name {
  font-weight: 600;
  color: #303133;
}

.goods-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}

.amount-text {
  color: #ef4238;
  font-weight: 700;
}

.evaluate-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.evaluate-text {
  color: #303133;
  line-height: 1.6;
}
</style>
