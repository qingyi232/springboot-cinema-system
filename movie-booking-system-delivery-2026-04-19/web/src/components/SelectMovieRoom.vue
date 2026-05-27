<template>
  <div class="select-room-page">
    <el-steps :active="1" align-center class="step-bar">
      <el-step title="选择场次" description="选择影院影厅与放映时间" />
      <el-step title="选择座位" description="挑选座位，支持预约锁座或直接支付" />
      <el-step title="出示票码" description="支付成功后去“我的订单”查看票码并到影院核销" />
    </el-steps>

    <el-alert type="info" :closable="false" class="page-tip">
      <template #title>
        已支持模拟支付 + 预约锁座：支付宝、微信、银行卡均可用；锁座订单会保留 15 分钟。
      </template>
    </el-alert>

    <template v-if="availableMovieRoom.length > 0">
      <el-card v-for="item in availableMovieRoom" :key="item.id" class="room-card" shadow="hover">
        <div class="room-row">
          <div class="room-main">
            <div class="room-title-row">
              <h4 class="room-name">{{ item.name }}</h4>
              <el-tag type="success" effect="plain" round>可选座</el-tag>
              <el-tag type="warning" effect="plain" round>支持锁座</el-tag>
            </div>
            <el-space wrap class="room-labels">
              <el-tag type="primary" effect="plain" v-for="label in splitLabels(item.label)" :key="label">
                {{ label }}
              </el-tag>
            </el-space>
          </div>

          <div class="time-box">
            <div class="time-title">放映时间</div>
            <div>开场：{{ item.start_time }}</div>
            <div>散场：{{ item.end_time }}</div>
          </div>

          <div class="price-action">
            <div class="room-price-box">
              <span class="room-price-label">单张票价</span>
              <el-text class="room-price" size="large">{{ formatSingleTicketPrice(item.price) }}</el-text>
            </div>
            <el-button type="danger" size="large" round @click="buy(item)">选座购票</el-button>
          </div>
        </div>
      </el-card>
    </template>
    <el-empty v-else description="暂无放映安排" />

    <el-dialog v-model="dialogOpen" v-if="dialogOpen" title="选择座位并结算" width="1100" append-to-body>
      <MovieSeatSelection :screeningPlanId="screeningPlanId" :cinemaId="props.cinemaId" @success="handleSeatSuccess" />
    </el-dialog>
  </div>
</template>

<script setup>
import {defineProps, ref, watch} from "vue";
import request from "@/utils/http.js";
import MovieSeatSelection from "@/components/MovieSeatSelection.vue";
import {formatSingleTicketPrice} from "@/utils/price.js";

const props = defineProps({
  cinemaId: {
    type: Number,
    default: 0
  },
  movieId: {
    type: Number,
    default: 0
  }
});

const availableMovieRoom = ref([]);
const dialogOpen = ref(false);
const screeningPlanId = ref();

watch(
  () => [props.cinemaId, props.movieId],
  () => {
    if (props.cinemaId && props.movieId) {
      getAvailableMovieRoom();
    }
  },
  {immediate: true}
);

function getAvailableMovieRoom() {
  request.get(`/screeningPlan/selectAvailableMovieRoom/${props.cinemaId}/${props.movieId}`).then((res) => {
    availableMovieRoom.value = res.data || [];
  });
}

function splitLabels(label) {
  if (!label) return [];
  return label.split(',').filter(Boolean);
}

function buy(item) {
  dialogOpen.value = true;
  screeningPlanId.value = item.id;
}

function handleSeatSuccess() {
  dialogOpen.value = false;
}
</script>

<style scoped>
.step-bar {
  margin-bottom: 16px;
}

.page-tip {
  margin-bottom: 16px;
}

.room-card {
  margin-bottom: 14px;
  border-radius: 16px;
}

.room-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
}

.room-main {
  flex: 1;
}

.room-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.room-name {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.room-labels {
  max-width: 100%;
}

.time-box {
  color: #606266;
  line-height: 1.8;
  min-width: 200px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f8fafc;
}

.time-title {
  margin-bottom: 4px;
  font-size: 12px;
  color: #909399;
}

.price-action {
  display: flex;
  align-items: center;
  gap: 18px;
}

.room-price-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  min-width: 110px;
}

.room-price-label {
  font-size: 12px;
  color: #909399;
}

.room-price {
  color: #f56c6c;
  font-weight: 600;
  font-size: 22px;
}
</style>
