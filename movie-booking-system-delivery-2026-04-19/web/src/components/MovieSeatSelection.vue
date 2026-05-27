<template>
  <div class="seat-selection-page">
    <el-card class="summary-card" v-if="screeningPlan.id">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="电影">{{ movieInfo.name || screeningPlan.movieName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="影院">{{ cinemaInfo.nickname || screeningPlan.cinemaName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="影厅">{{ movieRoom.name || screeningPlan.movieRoomName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单张票价">{{ formatSingleTicketPrice(screeningPlan.price) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ screeningPlan.startTime || screeningPlan.start_time }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ screeningPlan.endTime || screeningPlan.end_time }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-row :gutter="20" style="margin-top: 16px">
      <el-col :span="16">
        <el-card class="seat-card">
          <div class="screen">银幕中央</div>

          <div class="legend">
            <span><i class="seat-dot seat-available"></i> 可选</span>
            <span><i class="seat-dot seat-selected"></i> 已选</span>
            <span><i class="seat-dot seat-sold"></i> 已售</span>
          </div>

          <div class="seat-area">
            <div v-for="(row, rowIndex) in seatList" :key="`row-${rowIndex}`" class="seat-row">
              <span class="row-label">{{ rowIndex + 1 }}排</span>
              <el-button
                v-for="seat in row"
                :key="`${seat.numberOfX}-${seat.numberOfY}`"
                size="small"
                :type="seatType(seat)"
                plain
                :disabled="seat.disabled"
                class="seat-btn"
                @click="toggleSeat(seat)"
              >
                {{ seat.numberOfY }}
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="order-card">
          <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
            <div>
              <div class="panel-title">已选座位</div>
              <el-space wrap>
                <el-tag type="danger" v-for="item in seatSelected" :key="`${item.numberOfX}-${item.numberOfY}`">
                  {{ item.numberOfX }}排{{ item.numberOfY }}座
                </el-tag>
              </el-space>
              <div v-if="!seatSelected.length" class="panel-empty">请先选择座位</div>
            </div>

            <div class="order-summary">
              <div class="summary-row">
                <span>票数</span>
                <span>{{ seatSelected.length }} 张</span>
              </div>
              <div class="summary-row">
                <span>单张票价</span>
                <span>{{ formatSingleTicketPrice(screeningPlan.price) }}</span>
              </div>
              <div class="summary-row summary-row--amount">
                <span>订单金额</span>
                <span>￥{{ sumMoviePrice }}</span>
              </div>
            </div>

            <div style="width: 100%">
              <div class="panel-title">支付方式</div>
              <el-radio-group v-model="paymentMethod" class="payment-group">
                <el-radio-button label="支付宝">支付宝</el-radio-button>
                <el-radio-button label="微信">微信</el-radio-button>
                <el-radio-button label="银行卡">银行卡</el-radio-button>
              </el-radio-group>
              <div class="payment-tip">支持两种链路：先预约锁座 15 分钟，再去“我的订单”支付；或直接支付并生成票码。</div>
            </div>

            <div class="action-stack">
              <el-button plain type="primary" :disabled="!seatSelected.length" :loading="reserveLoading" @click="handleReserve">
                预约锁座 15 分钟
              </el-button>
              <el-button type="danger" :disabled="!seatSelected.length" :loading="submitLoading" @click="handleSubmit">
                直接支付并出票
              </el-button>
            </div>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {computed, defineEmits, defineProps, ref} from "vue";
import request from "@/utils/http.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {formatOrderAmount, formatSingleTicketPrice} from "@/utils/price.js";

const props = defineProps({
  screeningPlanId: {
    type: Number,
    default: 0
  },
  cinemaId: {
    type: Number,
    default: 0
  }
});

const emit = defineEmits(['success']);

const screeningPlan = ref({});
const movieRoom = ref({});
const movieInfo = ref({});
const cinemaInfo = ref({});
const movieOrder = ref([]);
const seatList = ref([]);
const paymentMethod = ref('支付宝');
const reserveLoading = ref(false);
const submitLoading = ref(false);

init();

function init() {
  request.get('/screeningPlan/selectById/' + props.screeningPlanId).then(async (res) => {
    screeningPlan.value = res.data || {};

    const roomReq = request.get('/movieRoom/selectById/' + screeningPlan.value.movieRoomId);
    const movieReq = request.get('/movie/selectById/' + screeningPlan.value.movieId);
    const cinemaReq = request.get('/cinema/selectById/' + (props.cinemaId || screeningPlan.value.cinemaId));

    const [roomRes, movieRes, cinemaRes] = await Promise.all([roomReq, movieReq, cinemaReq]);
    movieRoom.value = roomRes.data || {};
    movieInfo.value = movieRes.data || {};
    cinemaInfo.value = cinemaRes.data || {};

    buildSeatList();
    getMovieOrder();
  });
}

function getMovieOrder() {
  request.get('/movieOrder/soldSeats/' + props.screeningPlanId).then((res) => {
    movieOrder.value = res.data || [];

    for (let i = 0; i < movieOrder.value.length; i++) {
      const x = Number(movieOrder.value[i].numberOfX) - 1;
      const y = Number(movieOrder.value[i].numberOfY) - 1;
      if (seatList.value[x] && seatList.value[x][y]) {
        seatList.value[x][y].disabled = true;
        seatList.value[x][y].checked = false;
      }
    }
  });
}

function buildSeatList() {
  seatList.value = [];
  const rowCount = Number(movieRoom.value.numberOfX || 0);
  const colCount = Number(movieRoom.value.numberOfY || 0);
  for (let i = 0; i < rowCount; i++) {
    const row = [];
    for (let j = 0; j < colCount; j++) {
      row.push({
        numberOfX: i + 1,
        numberOfY: j + 1,
        checked: false,
        disabled: false
      });
    }
    seatList.value.push(row);
  }
}

function seatType(seat) {
  if (seat.disabled) return 'info';
  if (seat.checked) return 'danger';
  return 'default';
}

function toggleSeat(seat) {
  if (seat.disabled) return;
  seat.checked = !seat.checked;
}

const seatSelected = computed(() => {
  const selected = [];
  for (const row of seatList.value) {
    for (const seat of row) {
      if (seat.checked) {
        selected.push({
          numberOfX: seat.numberOfX,
          numberOfY: seat.numberOfY
        });
      }
    }
  }
  return selected;
});

const sumMoviePrice = computed(() => {
  const total = seatSelected.value.length * Number(screeningPlan.value.price || 0);
  return formatOrderAmount(total, {digits: 2});
});

function buildPayload() {
  if (!seatSelected.value.length) {
    ElMessage.warning('请先选择座位');
    return null;
  }
  return {
    screeningPlanId: props.screeningPlanId,
    seats: seatSelected.value.map(item => ({
      numberOfX: item.numberOfX,
      numberOfY: item.numberOfY
    })),
    paymentMethod: paymentMethod.value
  };
}

function handleReserve() {
  const payload = buildPayload();
  if (!payload) {
    return;
  }
  reserveLoading.value = true;
  request.post('/movieOrder/reserve', payload).then(() => {
    ElMessage.success('预约成功，已锁座 15 分钟，可前往“我的订单”完成支付');
    emit('success');
    router.push('/movieOrder');
  }).finally(() => {
    reserveLoading.value = false;
  });
}

function handleSubmit() {
  const payload = buildPayload();
  if (!payload) {
    return;
  }
  if (!paymentMethod.value) {
    ElMessage.warning('请选择支付方式');
    return;
  }
  submitLoading.value = true;
  request.post('/movieOrder/submit', payload).then(() => {
    ElMessage.success('支付成功，已生成票码，请前往“我的订单”查看');
    emit('success');
    router.push('/movieOrder');
  }).finally(() => {
    submitLoading.value = false;
  });
}
</script>

<style scoped>
.seat-selection-page {
  min-height: 100%;
}

.summary-card {
  border-radius: 14px;
}

.seat-card,
.order-card {
  border-radius: 16px;
}

.screen {
  margin: 0 auto 14px;
  width: 70%;
  background: linear-gradient(to right, #ffecec, #ffd8d8);
  text-align: center;
  color: #f56c6c;
  padding: 8px;
  border-radius: 20px;
  font-weight: 600;
}

.legend {
  display: flex;
  gap: 18px;
  margin-bottom: 12px;
  color: #909399;
}

.seat-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 6px;
}

.seat-available {
  background: #dcdfe6;
}

.seat-selected {
  background: #f56c6c;
}

.seat-sold {
  background: #909399;
}

.seat-area {
  max-height: 420px;
  overflow: auto;
  padding: 8px 0 4px;
}

.seat-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.row-label {
  width: 42px;
  color: #606266;
  text-align: right;
}

.seat-btn {
  min-width: 36px;
}

.panel-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.panel-empty {
  color: #909399;
}

.payment-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.payment-tip {
  margin-top: 10px;
  color: #909399;
  line-height: 1.7;
  font-size: 13px;
}

.order-summary {
  width: 100%;
  border-radius: 14px;
  background: #f7f8fa;
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #606266;
}

.summary-row--amount {
  color: #ef4238;
  font-size: 18px;
  font-weight: 700;
}

.action-stack {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-stack :deep(.el-button) {
  width: 100%;
}
</style>
