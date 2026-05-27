<template>
  <div class="page-wrap">
    <el-card class="order-container" shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <div class="page-title">我的订单</div>
            <div class="page-subtitle">电影票订单与卖品订单统一收口，支持支付、验票、评价、删除和再来一单。</div>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeType">
        <el-tab-pane label="电影票订单" name="movieOrder">
          <el-tabs v-model="searchForm.status" @tab-change="onTabChange">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="待支付" name="待支付"></el-tab-pane>
            <el-tab-pane label="待取票" name="待取票"></el-tab-pane>
            <el-tab-pane label="已完成" name="已完成"></el-tab-pane>
            <el-tab-pane label="已取消" name="已取消"></el-tab-pane>
          </el-tabs>

          <el-row :gutter="16" v-if="listData.length > 0">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in listData" :key="item.id" class="order-col">
              <el-card shadow="hover" class="order-card">
                <template #header>
                  <div class="order-card-header">
                    <el-text tag="b" truncated style="max-width: 140px">{{ item.movieName || '未命名电影' }}</el-text>
                    <el-tag :type="getStatusTagType(item.status)" effect="plain" round>{{ item.status }}</el-tag>
                  </div>
                </template>

                <el-image :src="item.movieMainImg" class="movie-cover" fit="cover" />

                <div class="order-info">
                  <div class="info-row"><span class="label">影院</span><span class="value">{{ item.cinemaName || '--' }}</span></div>
                  <div class="info-row"><span class="label">影厅</span><span class="value">{{ item.movieRoomName || '--' }}</span></div>
                  <div class="info-row"><span class="label">开场</span><span class="value">{{ formatDateTime(item.startTime) }}</span></div>
                  <div class="info-row"><span class="label">散场</span><span class="value">{{ formatDateTime(item.endTime) }}</span></div>
                  <div class="info-row"><span class="label">座位</span><span class="value">{{ formatSeat(item) }}</span></div>
                  <div class="info-row"><span class="label">金额</span><span class="value price">￥{{ formatPrice(item.price) }}</span></div>
                  <div class="info-row">
                    <span class="label">支付方式</span>
                    <el-tag size="small" :type="getPaymentTagType(item.paymentMethod)">
                      {{ item.paymentMethod || (item.status === '待支付' ? '待选择' : '--') }}
                    </el-tag>
                  </div>
                </div>

                <div class="ticket-code-box" v-if="item.ticketCode">
                  <div class="ticket-code-label">票码</div>
                  <div class="ticket-code-value">{{ item.ticketCode }}</div>
                  <div class="ticket-code-tip">
                    {{ item.status === '待取票' ? '请在影院出示票码完成验票核销' : item.status === '已完成' ? '该订单已完成验票' : '支付成功后生成的票码将保留在订单中' }}
                  </div>
                </div>

                <div class="reserve-box" v-if="item.status === '待支付' && item.reserveExpireTime">
                  <el-alert type="warning" :closable="false">
                    <template #title>
                      锁座截止：{{ formatDateTime(item.reserveExpireTime) }}（{{ getReserveCountdown(item.reserveExpireTime) }}）
                    </template>
                  </el-alert>
                </div>

                <div class="verify-box" v-if="item.status === '已完成' && item.verifyTime">
                  <el-alert type="success" :closable="false">
                    <template #title>
                      已于 {{ formatDateTime(item.verifyTime) }} 完成验票
                    </template>
                  </el-alert>
                </div>

                <el-divider style="margin: 10px 0" />

                <div class="actions">
                  <el-button type="primary" size="small" v-if="item.status === '待支付'" @click="openPayDialog(item)">
                    付款
                  </el-button>

                  <el-button type="danger" plain size="small" v-if="['待支付', '待取票'].includes(item.status)" @click="cancel(item)">
                    取消
                  </el-button>

                  <el-button type="success" plain size="small" v-if="item.status === '待取票' && item.ticketCode" @click="copyTicketCode(item.ticketCode)">
                    复制票码
                  </el-button>

                  <el-button type="primary" plain size="small" v-if="!item.orderEvaluateId && item.status === '已完成'" @click="showAddEvaluate(item)">
                    写评价
                  </el-button>

                  <el-button type="success" plain size="small" v-if="item.orderEvaluateId" @click="showEvaluate(item)">
                    查看评价
                  </el-button>

                  <el-button type="danger" plain size="small" v-if="['已完成', '已取消'].includes(item.status)" @click="removeOrder(item)">
                    删除订单
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-empty v-else description="暂无电影票订单"></el-empty>

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
        </el-tab-pane>

        <el-tab-pane label="卖品订单" name="goodsOrder">
          <GoodsOrder :showHeader="false" v-if="activeType === 'goodsOrder'" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>

  <el-dialog v-model="payDialog.visible" title="选择支付方式" width="420px">
    <el-radio-group v-model="payDialog.paymentMethod" class="payment-group">
      <el-radio-button label="支付宝">支付宝</el-radio-button>
      <el-radio-button label="微信">微信</el-radio-button>
      <el-radio-button label="银行卡">银行卡</el-radio-button>
    </el-radio-group>

    <template #footer>
      <el-button @click="payDialog.visible = false">取消</el-button>
      <el-button type="primary" @click="submitPay">确认支付</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="movieOrderEvaluateDialogVisible" v-if="!movieOrderEvaluate.id" title="评价" width="800">
    <el-form :model="movieOrderEvaluate" label-width="80px">
      <el-form-item label="评分">
        <el-rate v-model="movieOrderEvaluate.rate" show-score text-color="#ff9900" score-template="{value} 分" />
      </el-form-item>
      <el-form-item label="详细内容">
        <el-input v-model="movieOrderEvaluate.content" type="textarea" />
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="addEvaluate">提交</el-button>
        <el-button @click="movieOrderEvaluateDialogVisible = false">取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>

  <el-dialog v-model="movieOrderEvaluateDialogVisible" v-if="movieOrderEvaluate.id" title="评价详情" width="500">
    <el-descriptions title="" column="1">
      <el-descriptions-item label="评分">
        <el-rate
          v-model="movieOrderEvaluate.rate"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value} 分"
        />
      </el-descriptions-item>
      <el-descriptions-item label="详细内容">{{ movieOrderEvaluate.content }}</el-descriptions-item>
      <el-descriptions-item label="评价时间">{{ movieOrderEvaluate.createTime }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import request from '@/utils/http.js';
import {onUnmounted, ref, toRaw} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {formatOrderAmount} from '@/utils/price.js';
import dayjs from 'dayjs';
import {useRoute} from 'vue-router';
import GoodsOrder from '@/views/front/personalCenter/GoodsOrder.vue';

const route = useRoute();
const activeType = ref(route.query.type === 'goodsOrder' ? 'goodsOrder' : 'movieOrder');
const listData = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = ref({
  status: '',
});
const nowTick = ref(Date.now());
const timer = setInterval(() => {
  nowTick.value = Date.now();
}, 1000);

const payDialog = ref({
  visible: false,
  orderId: null,
  paymentMethod: '支付宝',
});

getPageList();

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/movieOrder/page', {params: data}).then((res) => {
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

function onTabChange() {
  pageInfo.value.pageNum = 1;
  getPageList();
}

function openPayDialog(row) {
  payDialog.value.orderId = row.id;
  payDialog.value.paymentMethod = row.paymentMethod || '支付宝';
  payDialog.value.visible = true;
}

function submitPay() {
  request.post('/movieOrder/pay/' + payDialog.value.orderId, {
    paymentMethod: payDialog.value.paymentMethod,
  }).then(() => {
    ElMessage.success('支付成功，已生成票码，请到影院验票');
    payDialog.value.visible = false;
    getPageList();
  });
}

function cancel(row) {
  const tip = row.status === '待取票' ? '取消后会退回已支付金额，并释放票码。是否继续？' : '确定取消当前订单吗？';
  ElMessageBox.confirm(tip, '取消订单', {
    confirmButtonText: '确定',
    cancelButtonText: '再想想',
    type: 'warning'
  }).then(() => {
    request.post('/movieOrder/cancel/' + row.id).then(() => {
      ElMessage.success('订单已取消');
      getPageList();
    });
  }).catch(() => {});
}

function removeOrder(row) {
  ElMessageBox.confirm('删除后订单记录将不再显示，是否继续？', '删除订单', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete('/movieOrder/delBatch', {data: [row.id]}).then(() => {
      ElMessage.success('订单已删除');
      if (listData.value.length === 1 && pageInfo.value.pageNum > 1) {
        pageInfo.value.pageNum -= 1;
      }
      getPageList();
    });
  }).catch(() => {});
}

function copyTicketCode(ticketCode) {
  if (!ticketCode) {
    return;
  }
  if (navigator.clipboard?.writeText) {
    navigator.clipboard.writeText(ticketCode).then(() => {
      ElMessage.success('票码已复制');
    });
    return;
  }
  ElMessage.info(`票码：${ticketCode}`);
}

const movieOrderEvaluateDialogVisible = ref(false);
const movieOrderEvaluate = ref({});

function showAddEvaluate(row) {
  movieOrderEvaluate.value = {
    movieOrderId: row.id,
    movieId: row.movieId,
    rate: 5,
    content: ''
  };
  movieOrderEvaluateDialogVisible.value = true;
}

function addEvaluate() {
  if (!movieOrderEvaluate.value.rate) {
    ElMessage.warning('请先选择评分');
    return;
  }
  request.post('/movieOrderEvaluate/add', movieOrderEvaluate.value).then(() => {
    movieOrderEvaluateDialogVisible.value = false;
    ElMessage.success('评价成功');
    getPageList();
  });
}

function showEvaluate(row) {
  request.get('/movieOrderEvaluate/selectById/' + row.orderEvaluateId).then((res) => {
    movieOrderEvaluate.value = res.data;
    movieOrderEvaluateDialogVisible.value = true;
  });
}

function getStatusTagType(status) {
  if (status === '待支付') return 'warning';
  if (status === '待取票') return 'primary';
  if (status === '已完成') return 'success';
  if (status === '已取消') return 'info';
  return 'info';
}

function getPaymentTagType(paymentMethod) {
  if (paymentMethod === '支付宝') return 'success';
  if (paymentMethod === '微信') return 'primary';
  if (paymentMethod === '银行卡') return 'warning';
  return 'info';
}

function formatPrice(val) {
  return formatOrderAmount(val, {digits: 2});
}

function formatSeat(item) {
  if (!item.numberOfX || !item.numberOfY) {
    return '--';
  }
  return `${item.numberOfX}排${item.numberOfY}座`;
}

function formatDateTime(value) {
  if (!value) {
    return '--';
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
}

function getReserveCountdown(expireTime) {
  if (!expireTime) {
    return '--';
  }
  const diff = dayjs(expireTime).valueOf() - nowTick.value;
  if (diff <= 0) {
    return '即将释放';
  }
  const totalSeconds = Math.floor(diff / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `剩余 ${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
}

onUnmounted(() => {
  clearInterval(timer);
});
</script>

<style scoped>
.page-wrap {
  width: 74%;
  margin: 20px auto 0;
}

.order-container {
  border-radius: 22px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #6b7280;
}

.order-col {
  margin-bottom: 16px;
}

.order-card {
  height: 100%;
  border-radius: 16px;
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.movie-cover {
  width: 100%;
  aspect-ratio: 2 / 3;
  border-radius: 8px;
}

.order-info {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.info-row .label {
  font-size: 13px;
  color: #909399;
}

.info-row .value {
  text-align: right;
  color: #303133;
  flex: 1;
  min-width: 0;
}

.info-row .price {
  color: #ef4238;
  font-weight: 700;
}

.ticket-code-box {
  margin-top: 12px;
  border-radius: 12px;
  padding: 12px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.12), rgba(64, 158, 255, 0.04));
}

.ticket-code-label {
  color: #909399;
  font-size: 12px;
}

.ticket-code-value {
  margin-top: 6px;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #303133;
  word-break: break-all;
}

.ticket-code-tip {
  margin-top: 6px;
  color: #606266;
  font-size: 12px;
  line-height: 1.7;
}

.reserve-box,
.verify-box {
  margin-top: 12px;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.payment-group {
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
