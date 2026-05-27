<template>
  <div class="movie-card" :class="[`movie-card--${cardMode}`, { 'movie-card--upcoming': upcoming, 'movie-card--disabled': unavailable } ]">
    <div class="movie-card-main" @click="openDetails">
      <el-image class="movie-cover" fit="cover" :src="props.info.mainImg">
        <template #error>
          <div class="cover-fallback">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-image>

      <div class="movie-body">
        <div class="movie-name-row">
          <el-text size="large" tag="b" truncated class="movie-name">
            {{ props.info.name }}
          </el-text>
          <div class="score-pill">
            <el-icon><StarFilled /></el-icon>
            <span>{{ formatScore(props.info.score) }}</span>
          </div>
        </div>

        <div class="movie-meta-row">
          <span class="movie-meta-item">{{ props.info.movieTypeName || '影片' }}</span>
          <span class="movie-meta-dot">·</span>
          <span class="movie-meta-item">{{ props.info.movieRegionName || '地区待定' }}</span>
        </div>

        <div class="movie-info-strip" :class="{ 'movie-info-strip--upcoming': upcoming, 'movie-info-strip--disabled': unavailable }">
          <template v-if="upcoming">
            <span class="strip-label">上映日期</span>
            <span class="strip-value">{{ props.info.releaseDate || '待定' }}</span>
          </template>
          <template v-else-if="unavailable">
            <span class="strip-label">售卖状态</span>
            <span class="strip-value">暂未排片</span>
          </template>
          <template v-else>
            <span class="strip-label">购票参考价</span>
            <span class="strip-value">{{ displayTicketPrice }}</span>
          </template>
        </div>
      </div>
    </div>

    <el-button
      class="action-btn"
      :class="[`action-btn--${actionTone}`, { 'action-btn--plain': upcoming && props.info.movieCollectId }]"
      :disabled="unavailable"
      @click.stop="handleAction"
    >
      {{ actionLabel }}
    </el-button>

    <el-dialog v-model="quickBuyDialogOpen" title="选座购票" width="900" append-to-body>
      <template v-if="availableCinemaList.length > 0">
        <el-alert title="请选择电影院，再选择影厅和座位" type="info" :closable="false" style="margin-bottom: 16px" />

        <el-radio-group v-model="selectedCinemaId" style="width: 100%; margin-bottom: 16px">
          <el-space wrap>
            <el-radio-button v-for="cinema in availableCinemaList" :key="cinema.cinema_id" :label="cinema.cinema_id">
              {{ cinema.nickname }}
            </el-radio-button>
          </el-space>
        </el-radio-group>

        <SelectMovieRoom
          v-if="selectedCinemaId"
          :cinemaId="selectedCinemaId"
          :movieId="props.info.id"
          :key="`${props.info.id}-${selectedCinemaId}`"
        />
      </template>
      <el-empty v-else description="该电影暂无可购场次" />
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, ref} from 'vue';
import request from '@/utils/http.js';
import router from '@/router/index.js';
import {ElMessage} from 'element-plus';
import {Picture, StarFilled} from '@element-plus/icons-vue';
import SelectMovieRoom from '@/components/SelectMovieRoom.vue';
import {formatTicketPrice} from '@/utils/price.js';
import {getMovieAvailabilityState} from '@/utils/movie.js';

const props = defineProps({
  info: {
    type: Object,
    default: () => ({})
  },
  mode: {
    type: String,
    default: 'default'
  }
});

const quickBuyDialogOpen = ref(false);
const availableCinemaList = ref([]);
const selectedCinemaId = ref();

const availabilityState = computed(() => {
  if (props.mode === 'upcoming') {
    return 'upcoming';
  }
  return getMovieAvailabilityState(props.info);
});
const upcoming = computed(() => availabilityState.value === 'upcoming');
const unavailable = computed(() => availabilityState.value === 'unavailable');
const cardMode = computed(() => {
  if (props.mode !== 'default' && !(props.mode === 'hot' && unavailable.value) && !(props.mode === 'guess' && unavailable.value)) {
    return props.mode;
  }
  if (upcoming.value) {
    return 'upcoming';
  }
  if (unavailable.value) {
    return 'disabled';
  }
  return 'hot';
});
const actionTone = computed(() => {
  if (unavailable.value) return 'disabled';
  if (upcoming.value) return 'warning';
  if (props.mode === 'guess') return 'primary';
  return 'danger';
});
const displayTicketPrice = computed(() => formatTicketPrice(props.info.lowestPrice, {fallback: '待排片'}));
const actionLabel = computed(() => {
  if (upcoming.value) {
    return props.info.movieCollectId ? '取消想看' : '想看';
  }
  if (unavailable.value) {
    return '暂不可购';
  }
  return '购票';
});

function openDetails() {
  router.push('/movieDetails/' + props.info.id);
}

function handleAction() {
  if (upcoming.value) {
    toggleWish();
    return;
  }
  if (unavailable.value) {
    ElMessage.warning('该电影暂未开放场次');
    return;
  }
  openQuickBuy();
}

function openQuickBuy() {
  if (availabilityState.value !== 'buyable') {
    ElMessage.warning('该电影暂未开售');
    return;
  }
  request.get('/screeningPlan/selectAvailableCinema/' + props.info.id).then(res => {
    availableCinemaList.value = res.data || [];
    if (!availableCinemaList.value.length) {
      ElMessage.warning('该电影暂无可购场次');
      return;
    }
    selectedCinemaId.value = availableCinemaList.value[0].cinema_id;
    quickBuyDialogOpen.value = true;
  });
}

function toggleWish() {
  if (props.info.movieCollectId) {
    request.delete('/movieCollect/delBatch', {data: [props.info.movieCollectId]}).then(() => {
      props.info.movieCollectId = null;
      ElMessage.success('已取消想看');
    });
    return;
  }
  request.post('/movieCollect/add', {movieId: props.info.id}).then((res) => {
    props.info.movieCollectId = res.data?.id;
    ElMessage.success('已加入想看');
  });
}

function formatScore(value) {
  const numericValue = Number(value);
  if (!Number.isFinite(numericValue) || numericValue <= 0) {
    return '暂无';
  }
  return `${numericValue.toFixed(1).replace(/\.0$/, '')}分`;
}
</script>

<style scoped>
.movie-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 10px;
  border-radius: 20px;
  border: 1px solid #f1f2f5;
  background: #fff;
  transition: transform .25s ease, box-shadow .25s ease;
}

.movie-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 30px rgba(15, 23, 42, 0.08);
}

.movie-card--disabled {
  background: linear-gradient(180deg, #fafafa, #f3f4f6);
  border-color: #e5e7eb;
}

.movie-card--disabled:hover {
  transform: none;
  box-shadow: none;
}

.movie-card-main {
  flex: 1;
  cursor: pointer;
}

.movie-cover {
  width: 100%;
  aspect-ratio: 2 / 3;
  border-radius: 14px;
  overflow: hidden;
}

.movie-card--upcoming .movie-cover {
  filter: saturate(0.92);
}

.movie-card--disabled .movie-cover {
  filter: grayscale(1);
  opacity: 0.75;
}

.cover-fallback {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
}

.movie-body {
  padding-top: 12px;
}

.movie-name-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.movie-name {
  flex: 1;
  min-width: 0;
  color: #1f2937;
}

.score-pill {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(245, 158, 11, 0.12);
  color: #d97706;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.movie-meta-row {
  margin-top: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
}

.movie-meta-dot {
  color: #d1d5db;
}

.movie-info-strip {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.08), rgba(248, 113, 113, 0.03));
}

.movie-info-strip--upcoming {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.14), rgba(251, 191, 36, 0.04));
}

.movie-info-strip--disabled {
  background: linear-gradient(135deg, rgba(107, 114, 128, 0.14), rgba(229, 231, 235, 0.28));
}

.strip-label {
  font-size: 12px;
  color: #6b7280;
}

.strip-value {
  font-size: 15px;
  font-weight: 700;
  color: #ef4444;
}

.movie-info-strip--upcoming .strip-value {
  color: #d97706;
}

.movie-info-strip--disabled .strip-value {
  color: #6b7280;
}

.action-btn {
  width: 100%;
  margin-top: 14px;
  height: 42px;
  border-radius: 14px;
  border: none;
  font-size: 15px;
  font-weight: 700;
}

.action-btn--danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: #fff;
}

.action-btn--primary {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: #fff;
}

.action-btn--warning {
  background: linear-gradient(135deg, #f59e0b, #facc15);
  color: #fff;
}

.action-btn--disabled,
.action-btn:disabled {
  background: #e5e7eb;
  color: #9ca3af;
  border: 1px solid #d1d5db;
}

.action-btn--plain.action-btn--warning {
  background: rgba(245, 158, 11, 0.12);
  color: #d97706;
  border: 1px solid rgba(245, 158, 11, 0.35);
}
</style>
