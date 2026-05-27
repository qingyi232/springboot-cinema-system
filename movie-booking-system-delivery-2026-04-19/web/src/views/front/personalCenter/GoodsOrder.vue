<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <el-card shadow="never" v-if="showHeader">
        <div class="header-row">
          <div>
            <div class="page-title">卖品订单</div>
            <div class="page-subtitle">支持删除订单、写好评、再来一单，后台会同步显示评价内容。</div>
          </div>
        </div>

        <el-form :model="searchForm" inline style="margin-top: 16px">
          <el-form-item label="商品名称">
            <el-input v-model="searchForm.goodsName" clearable placeholder="搜索商品名称" @keyup.enter="search" />
          </el-form-item>
          <el-form-item label="支付方式">
            <el-select v-model="searchForm.paymentMethod" clearable placeholder="全部" style="width: 140px">
              <el-option label="支付宝" value="支付宝" />
              <el-option label="微信" value="微信" />
              <el-option label="银行卡" value="银行卡" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="search">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never">
        <el-alert
          title="卖品订单已并入“我的订单”，购买后即时完成；你可以删除记录、写好评，或者按原数量再来一单。"
          type="info"
          :closable="false"
          style="margin-bottom: 16px"
        />

        <el-table :data="listData" border style="width: 100%">
          <el-table-column prop="id" label="订单ID" width="90" />
          <el-table-column label="商品信息" min-width="280">
            <template #default="scope">
              <div class="goods-cell">
                <el-image :src="scope.row.goodsMainImg" class="goods-thumb" fit="cover" />
                <div class="goods-meta">
                  <div class="goods-name">{{ scope.row.goodsName || '--' }}</div>
                  <div class="goods-sub">数量 {{ scope.row.quantity || 0 }} 件</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="110">
            <template #default="scope">￥{{ formatPrice(scope.row.unitPrice) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="90" />
          <el-table-column label="订单金额" width="120">
            <template #default="scope"><span class="amount-text">￥{{ formatPrice(scope.row.totalPrice) }}</span></template>
          </el-table-column>
          <el-table-column label="支付方式" width="110">
            <template #default="scope">
              <el-tag size="small" :type="getPaymentTagType(scope.row.paymentMethod)" effect="plain" round>
                {{ scope.row.paymentMethod || '--' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="评价" min-width="180">
            <template #default="scope">
              <div v-if="scope.row.evaluateTime" class="evaluate-cell">
                <el-rate :model-value="Number(scope.row.evaluateRate || 0)" disabled />
                <div class="evaluate-text">{{ scope.row.evaluateContent || '好评' }}</div>
              </div>
              <span v-else class="goods-sub">暂未评价</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="下单时间" min-width="170" />
          <el-table-column label="操作" min-width="240" fixed="right">
            <template #default="scope">
              <div class="action-wrap">
                <el-button type="primary" plain @click="reOrder(scope.row)">再来一单</el-button>
                <el-button v-if="!scope.row.evaluateTime" type="warning" plain @click="openEvaluate(scope.row)">写好评</el-button>
                <el-button v-else type="success" plain @click="viewEvaluate(scope.row)">查看评价</el-button>
                <el-button type="danger" plain @click="removeOrder(scope.row)">删除订单</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="listData.length === 0" description="暂无卖品订单" style="padding-top: 20px" />

        <div class="pagination-wrap">
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

    <el-dialog v-model="evaluateDialog.visible" :title="evaluateDialog.readonly ? '评价详情' : '写好评'" width="500px">
      <el-form label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="evaluateDialog.form.evaluateRate" :disabled="evaluateDialog.readonly" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="evaluateDialog.form.evaluateContent" :disabled="evaluateDialog.readonly" type="textarea" :rows="4" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="评价时间" v-if="evaluateDialog.readonly && evaluateDialog.form.evaluateTime">
          <span>{{ evaluateDialog.form.evaluateTime }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluateDialog.visible = false">关闭</el-button>
        <el-button v-if="!evaluateDialog.readonly" type="primary" @click="submitEvaluate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/utils/http.js';
import {ref, toRaw} from 'vue';
import {ElMessage} from 'element-plus';
import {formatOrderAmount} from '@/utils/price.js';

const props = defineProps({
  showHeader: {
    type: Boolean,
    default: true
  }
});

const listData = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const searchForm = ref({
  goodsName: '',
  paymentMethod: ''
});
const evaluateDialog = ref({
  visible: false,
  readonly: false,
  orderId: null,
  form: {
    evaluateRate: 5,
    evaluateContent: '好评'
  }
});

const showHeader = props.showHeader;

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
  searchForm.value = {goodsName: '', paymentMethod: ''};
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

function getPaymentTagType(paymentMethod) {
  if (paymentMethod === '支付宝') return 'success';
  if (paymentMethod === '微信') return 'primary';
  if (paymentMethod === '银行卡') return 'warning';
  return 'info';
}

function formatPrice(value) {
  return formatOrderAmount(value, {digits: 2});
}

function removeOrder(row) {
  request.delete('/goodsOrder/delBatch', {data: [row.id]}).then(() => {
    ElMessage.success('卖品订单已删除');
    if (listData.value.length === 1 && pageInfo.value.pageNum > 1) {
      pageInfo.value.pageNum -= 1;
    }
    getPageList();
  });
}

function reOrder(row) {
  request.post('/goodsOrder/submit', {
    goodsId: row.goodsId,
    quantity: row.quantity,
    paymentMethod: row.paymentMethod || '支付宝'
  }).then(() => {
    ElMessage.success('已按原订单重新下单');
    getPageList();
  });
}

function openEvaluate(row) {
  evaluateDialog.value = {
    visible: true,
    readonly: false,
    orderId: row.id,
    form: {
      evaluateRate: 5,
      evaluateContent: '好评'
    }
  };
}

function viewEvaluate(row) {
  evaluateDialog.value = {
    visible: true,
    readonly: true,
    orderId: row.id,
    form: {
      evaluateRate: Number(row.evaluateRate || 0),
      evaluateContent: row.evaluateContent || '好评',
      evaluateTime: row.evaluateTime
    }
  };
}

function submitEvaluate() {
  request.put('/goodsOrder/evaluate/' + evaluateDialog.value.orderId, evaluateDialog.value.form).then(() => {
    ElMessage.success('评价成功');
    evaluateDialog.value.visible = false;
    getPageList();
  });
}
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}

.page-subtitle {
  margin-top: 8px;
  color: #6b7280;
  font-size: 14px;
}

.goods-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-thumb {
  width: 58px;
  height: 58px;
  border-radius: 12px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.goods-name {
  font-weight: 600;
  color: #111827;
}

.goods-sub {
  margin-top: 6px;
  color: #6b7280;
  font-size: 12px;
}

.amount-text {
  color: #ef4444;
  font-weight: 700;
}

.evaluate-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.evaluate-text {
  font-size: 12px;
  color: #374151;
  line-height: 1.6;
}

.action-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
