<template>
  <div class="page-wrap" v-if="info.id">
    <el-card class="page-card">
      <el-row :gutter="32">
        <el-col :span="8">
          <el-image :src="info.mainImg" class="movie-main-img" fit="cover" />
        </el-col>
        <el-col :span="16">
          <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
            <div class="title-row">
              <div>
                <h1 class="movie-title">{{ info.name }}</h1>
                <div class="movie-subline">
                  <el-tag :type="statusTagType" round>{{ purchaseStatus.tag }}</el-tag>
                  <el-text type="info">{{ purchaseStatus.tip }}</el-text>
                </div>
              </div>

              <el-space size="small">
                <el-button type="danger" size="large" round v-if="canBuyCurrentMovie" @click="openQuickBuy">
                  选座购票
                </el-button>
                <el-button class="wish-btn" size="large" round v-else-if="isUpcomingMovieState" @click="toggleWish">
                  {{ info.movieCollectId ? '取消想看' : '想看' }}
                </el-button>
                <el-button size="large" round disabled v-else>
                  暂不可购
                </el-button>
              </el-space>
            </div>

            <el-descriptions title="" :column="1" class="movie-meta">
              <el-descriptions-item label="导演" label-align="center">{{ info.director || '--' }}</el-descriptions-item>
              <el-descriptions-item label="主演" label-align="center">{{ info.protagonist || '--' }}</el-descriptions-item>
              <el-descriptions-item label="上映日期" label-align="center">{{ info.releaseDate || '--' }}</el-descriptions-item>
              <el-descriptions-item label="上映地区" label-align="center">{{ info.movieRegionName || '--' }}</el-descriptions-item>
              <el-descriptions-item label="时长" label-align="center">{{ info.duration || '--' }}分钟</el-descriptions-item>
            </el-descriptions>

            <div class="movie-highlight-grid">
              <div class="highlight-card">
                <span class="highlight-label">评分</span>
                <span class="highlight-value">{{ formatScore(info.score) }}</span>
              </div>
              <div class="highlight-card highlight-card--accent" :class="{ 'highlight-card--muted': !canBuyCurrentMovie, 'highlight-card--disabled': isUnavailableMovieState }">
                <span class="highlight-label">{{ priceBlock.label }}</span>
                <span class="highlight-value">{{ priceBlock.value }}</span>
              </div>
              <div class="highlight-card">
                <span class="highlight-label">累计票房</span>
                <span class="highlight-value">{{ formatBoxOffice(info.boxOffice) }}</span>
              </div>
            </div>
          </el-space>
        </el-col>
      </el-row>

      <el-divider />

      <el-alert :type="canBuyCurrentMovie ? 'success' : 'warning'" :closable="false" show-icon class="flow-alert">
        <template #title>
          {{ flowText }}
        </template>
      </el-alert>

      <div class="movie-section">
        <h3>剧情简介</h3>
        <div class="intro-box" v-html="info.intro"></div>
      </div>

      <div class="movie-section" v-if="info.imgs">
        <div class="section-title-row">
          <h3>剧照</h3>
          <el-text type="info">支持点击大图预览</el-text>
        </div>
        <el-space wrap size="large">
          <el-image
            v-for="(img, index) in info.imgs.split(',')"
            :key="img + index"
            :src="img"
            class="gallery-image"
            :preview-src-list="info.imgs.split(',')"
            :initial-index="index"
          />
        </el-space>
      </div>

      <div class="movie-section" v-if="canBuyCurrentMovie">
        <div class="section-title-row">
          <h3>可选影院</h3>
          <el-text type="info">
            {{ availableCinemaList.length > 0 ? '先选择影院，再进入影厅、场次与座位页面' : '当前暂无可购票影院' }}
          </el-text>
        </div>

        <div v-if="availableCinemaList.length > 0" class="cinema-list">
          <el-card v-for="item in availableCinemaList" :key="item.cinema_id" shadow="never" class="cinema-item">
            <div class="cinema-item-row">
              <div class="cinema-main">
                <div class="cinema-title-row">
                  <h4 class="cinema-name">{{ item.nickname }}</h4>
                  <el-tag type="success" effect="plain" round>可购票</el-tag>
                </div>
                <el-text>{{ item.address }}</el-text>
                <div class="cinema-labels">
                  <el-space wrap>
                    <el-tag type="primary" effect="plain" v-for="(label, idx) in splitLabels(item.label)" :key="label + idx">
                      {{ label }}
                    </el-tag>
                  </el-space>
                </div>
              </div>

              <div class="cinema-action">
                <div class="cinema-price-box">
                  <span class="cinema-price-label">参考票价</span>
                  <el-text class="cinema-price">{{ formatTicketPrice(item.price) }}</el-text>
                </div>
                <el-button type="primary" size="large" round @click="buy(item)">选择该影院</el-button>
              </div>
            </div>
          </el-card>
        </div>

        <el-empty v-else description="该电影暂无放映安排，后续开放排期后即可购票" />
      </div>

      <div class="movie-section">
        <div class="section-title-row">
          <h3>观众评价</h3>
          <el-text type="info">已完成订单的评价会同步展示到这里和后台</el-text>
        </div>
        <MovieOrderEvaluate :movieId="id" />
      </div>
    </el-card>

    <el-dialog v-model="selectMovieRoomDialogOpen" v-if="selectMovieRoomDialogOpen" title="第2步：请选择影厅和场次" width="860">
      <SelectMovieRoom :cinemaId="cinemaId" :movieId="id" />
    </el-dialog>
  </div>
</template>

<script setup>
import request from '@/utils/http.js';
import {computed, ref} from 'vue';
import {useRoute} from 'vue-router';
import {ElMessage} from 'element-plus';
import SelectMovieRoom from '@/components/SelectMovieRoom.vue';
import MovieOrderEvaluate from '@/components/MovieOrderEvaluate.vue';
import {formatBoxOffice, formatTicketPrice} from '@/utils/price.js';
import {getMovieAvailabilityState} from '@/utils/movie.js';

const route = useRoute();
const id = ref(route.params.id);
const info = ref({});
const availableCinemaList = ref([]);
const selectMovieRoomDialogOpen = ref(false);
const cinemaId = ref();

const availabilityState = computed(() => getMovieAvailabilityState(info.value));
const canBuyCurrentMovie = computed(() => availabilityState.value === 'buyable');
const isUpcomingMovieState = computed(() => availabilityState.value === 'upcoming');
const isUnavailableMovieState = computed(() => availabilityState.value === 'unavailable');
const statusTagType = computed(() => {
  if (canBuyCurrentMovie.value) {
    return 'danger';
  }
  if (isUpcomingMovieState.value) {
    return 'warning';
  }
  return 'info';
});
const purchaseStatus = computed(() => {
  if (canBuyCurrentMovie.value) {
    return {
      tag: '在线选座',
      tip: '当前已有可售场次，可直接选择影院和影厅完成下单'
    };
  }
  if (isUpcomingMovieState.value) {
    return {
      tag: '即将上映',
      tip: '当前影片还未开售，可先加入想看，开售后将从首页和列表页直接购票'
    };
  }
  return {
    tag: '暂未排片',
    tip: '当前影片暂无可售场次，后续排片上架后即可直接购票'
  };
});
const priceBlock = computed(() => {
  if (canBuyCurrentMovie.value) {
    return {
      label: '参考票价',
      value: formatTicketPrice(info.value.lowestPrice)
    };
  }
  if (isUpcomingMovieState.value) {
    return {
      label: '上映状态',
      value: '即将上映'
    };
  }
  return {
    label: '售卖状态',
    value: '暂未排片'
  };
});
const flowText = computed(() => {
  if (canBuyCurrentMovie.value) {
    return '购票流程：1）先选影院 → 2）再选影厅和场次 → 3）选座并支付';
  }
  if (isUpcomingMovieState.value) {
    return '当前影片为即将上映状态，可先加入想看，待开放排片后再购票';
  }
  return '当前影片暂无可售场次，暂不支持想看或购票，待影院排片后会自动进入可购状态';
});

getInfo();
getAvailableCinemaList();
addBrowsingHistory();

function getInfo() {
  request.get('/movie/selectById/' + id.value).then((res) => {
    info.value = res.data || {};
  });
}

function getAvailableCinemaList() {
  request.get('/screeningPlan/selectAvailableCinema/' + id.value).then((res) => {
    availableCinemaList.value = res.data || [];
  });
}

function addBrowsingHistory() {
  request.post('/movieBrowsingHistory/add', {movieId: id.value}).then(() => {});
}

function toggleWish() {
  if (!isUpcomingMovieState.value) {
    ElMessage.warning('只有即将上映电影支持想看');
    return;
  }
  if (info.value.movieCollectId) {
    request.delete('/movieCollect/delBatch', {data: [info.value.movieCollectId]}).then(() => {
      info.value.movieCollectId = null;
      ElMessage.success('已取消想看');
    });
    return;
  }
  request.post('/movieCollect/add', {movieId: id.value}).then((res) => {
    info.value.movieCollectId = res.data?.id;
    ElMessage.success('已加入想看');
  });
}

function openQuickBuy() {
  if (!availableCinemaList.value.length) {
    ElMessage.warning('当前暂无可购票影院');
    return;
  }
  buy(availableCinemaList.value[0]);
}

function buy(item) {
  cinemaId.value = item.cinema_id;
  selectMovieRoomDialogOpen.value = true;
}

function splitLabels(labels) {
  if (!labels) {
    return [];
  }
  return labels.split(',').filter((item) => item && item.trim());
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
.page-wrap {
  width: 60%;
  margin: 20px auto 0;
}

.page-card {
  border-radius: 20px;
}

.movie-main-img {
  width: 100%;
  height: 420px;
  object-fit: cover;
  border-radius: 14px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.14);
}

.title-row {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.movie-title {
  margin: 0;
  color: #303133;
  font-size: 30px;
}

.movie-subline {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.wish-btn {
  background: rgba(245, 158, 11, 0.12);
  color: #d97706;
  border: 1px solid rgba(245, 158, 11, 0.35);
}

.movie-meta {
  margin-bottom: 4px;
}

.movie-highlight-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.highlight-card {
  border-radius: 14px;
  padding: 16px 18px;
  background: #f7f8fa;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.highlight-card--accent {
  background: linear-gradient(135deg, rgba(239, 66, 56, 0.14), rgba(239, 66, 56, 0.03));
}

.highlight-card--muted {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.16), rgba(245, 158, 11, 0.04));
}

.highlight-card--disabled {
  background: linear-gradient(135deg, rgba(107, 114, 128, 0.18), rgba(229, 231, 235, 0.28));
}

.highlight-label {
  color: #909399;
  font-size: 13px;
}

.highlight-value {
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.flow-alert {
  margin-bottom: 12px;
}

.movie-section {
  margin-top: 30px;
  padding: 0 20px;
}

.movie-section h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}

.intro-box {
  line-height: 1.9;
  color: #4b5563;
}

.gallery-image {
  width: 210px;
  height: 300px;
  border-radius: 12px;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cinema-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cinema-item {
  margin-bottom: 0;
  border-radius: 16px;
  border: 1px solid #ebeef5;
}

.cinema-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.cinema-main {
  flex: 1;
}

.cinema-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.cinema-name {
  margin: 0;
}

.cinema-labels {
  margin-top: 10px;
}

.cinema-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.cinema-price-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.cinema-price-label {
  color: #909399;
  font-size: 12px;
}

.cinema-price {
  color: #f56c6c;
  font-size: 22px;
  font-weight: 600;
}
</style>
