<template>
  <div class="page-wrap" v-if="info.id">
    <el-card class="page-card">
      <el-row :gutter="30">
        <el-col :span="6">
          <el-image :src="info.avatarUrl" class="cinema-cover" fit="cover" />
        </el-col>
        <el-col :span="18">
          <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
            <div class="cinema-title-row">
              <div>
                <h1 class="cinema-title">{{ info.nickname }}</h1>
                <div class="cinema-subline">
                  <el-tag type="danger" round>在线选座</el-tag>
                  <el-text type="info">已进入影院详情，继续选择影片、影厅与场次即可下单</el-text>
                </div>
              </div>
            </div>

            <el-descriptions title="" :column="1" class="cinema-desc">
              <el-descriptions-item label="电话" label-align="center">{{ info.tel || "--" }}</el-descriptions-item>
              <el-descriptions-item label="地址" label-align="center">{{ info.address || "--" }}</el-descriptions-item>
              <el-descriptions-item label="标签" label-align="center">
                <el-space wrap>
                  <el-tag
                    type="primary"
                    effect="plain"
                    v-for="(label, idx) in splitLabels(info.label)"
                    :key="label + idx"
                  >
                    {{ label }}
                  </el-tag>
                </el-space>
              </el-descriptions-item>
            </el-descriptions>
          </el-space>
        </el-col>
      </el-row>

      <el-divider />

      <el-alert type="info" :closable="false" show-icon class="flow-alert">
        <template #title>
          当前链路：已选影院（{{ info.nickname }}）→ 选择电影 → 选择影厅和场次 → 选座购票
        </template>
      </el-alert>

      <div class="movie-gallery">
        <div class="section-title-row">
          <h3>第1步：选择电影</h3>
          <el-text type="info">
            {{ movieList.length > 0 ? "这里只保留当前影院真正可售的影片，点选后立即查看场次" : "当前影院暂无可售影片" }}
          </el-text>
        </div>

        <div class="movie-carousel" v-if="movieList.length > 0">
          <div class="movie-list">
            <div
              v-for="item in movieList"
              :key="item.id"
              class="movie-item"
              :class="{ active: item.id === movieId }"
              @click="movieId = item.id"
            >
              <el-image class="movie-poster" fit="cover" :src="item.mainImg" />
              <div class="movie-item-body">
                <el-text size="large" tag="b" truncated class="movie-name">
                  {{ item.name }}
                </el-text>
                <div class="movie-item-meta">
                  <el-tag :type="item.id === movieId ? 'danger' : 'success'" size="small" effect="plain" round>
                    {{ item.id === movieId ? "当前选择" : "可购票" }}
                  </el-tag>
                  <span class="movie-price">{{ formatTicketPrice(item.lowestPrice) }}</span>
                </div>
                <div class="movie-next-time">最近场次：{{ item.nextStartTime }}</div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无电影"></el-empty>

        <div class="room-section">
          <div class="section-title-row">
            <h3>第2步：选择影厅和场次</h3>
            <el-text type="info" v-if="currentMovieName">
              当前影片：{{ currentMovieName }} · {{ currentMoviePrice }}
            </el-text>
          </div>

          <el-alert
            v-if="currentMovieName"
            type="success"
            :closable="false"
            class="room-alert"
          >
            <template #title>
              请继续选择 {{ currentMovieName }} 的影厅与场次，下一步即可进入座位与支付页面
            </template>
          </el-alert>

          <SelectMovieRoom v-if="movieId" :cinemaId="id" :movieId="movieId" :key="movieId" />
          <el-empty v-else description="该影院暂无可购电影" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {computed, ref} from "vue";
import {useRoute} from "vue-router";
import SelectMovieRoom from "@/components/SelectMovieRoom.vue";
import {formatTicketPrice} from "@/utils/price.js";
import dayjs from "dayjs";

const route = useRoute();
const id = ref(Number(route.params.id));
const info = ref({});
const movieList = ref([]);
const movieId = ref();

getInfo();
getMovieList();

function getInfo() {
  request.get("/cinema/selectById/" + id.value).then((res) => {
    info.value = res.data || {};
  });
}

function getMovieList() {
  Promise.all([
    request.get("/screeningPlan/page", {
      params: {
        cinemaId: id.value,
        pageNum: 1,
        pageSize: 1000
      }
    }),
    request.get("/movie/list")
  ]).then(([planRes, movieRes]) => {
    const plans = (planRes.data?.list || []).filter((item) => {
      const startTime = item.startTime || item.start_time;
      return Number(item.price || 0) > 0 && dayjs(startTime).isValid() && dayjs(startTime).isAfter(dayjs());
    });
    const sourceMovies = movieRes.data || [];
    const movieIds = [...new Set(plans.map((item) => item.movieId).filter(Boolean))];
    const lowestPriceMap = new Map();
    const nextStartTimeMap = new Map();

    plans.forEach((item) => {
      const price = Number(item.price);
      const startTime = item.startTime || item.start_time;
      if (Number.isFinite(price) && price > 0) {
        const currentPrice = lowestPriceMap.get(item.movieId);
        if (currentPrice === undefined || price < currentPrice) {
          lowestPriceMap.set(item.movieId, price);
        }
      }
      const currentStartTime = nextStartTimeMap.get(item.movieId);
      if (!currentStartTime || dayjs(startTime).isBefore(dayjs(currentStartTime))) {
        nextStartTimeMap.set(item.movieId, startTime);
      }
    });

    movieList.value = sourceMovies
      .filter((item) => movieIds.includes(item.id))
      .map((item) => ({
        ...item,
        lowestPrice: lowestPriceMap.get(item.id) ?? item.lowestPrice ?? null,
        nextStartTime: formatDateTime(nextStartTimeMap.get(item.id))
      }))
      .sort((a, b) => dayjs(a.nextStartTime).valueOf() - dayjs(b.nextStartTime).valueOf());

    movieId.value = movieList.value.length > 0 ? movieList.value[0].id : null;
  });
}

const currentMovie = computed(() => movieList.value.find((item) => item.id === movieId.value) || null);
const currentMovieName = computed(() => currentMovie.value?.name || "");
const currentMoviePrice = computed(() => formatTicketPrice(currentMovie.value?.lowestPrice));

function splitLabels(labels) {
  if (!labels) {
    return [];
  }
  return labels.split(",").filter((item) => item && item.trim());
}

function formatDateTime(value) {
  if (!value) {
    return '--';
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm');
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

.cinema-cover {
  width: 100%;
  height: 220px;
  object-fit: cover;
  border-radius: 14px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.cinema-title-row {
  width: 100%;
}

.cinema-title {
  margin: 0;
  color: #303133;
  font-size: 30px;
}

.cinema-subline {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.cinema-desc {
  margin-bottom: 8px;
}

.flow-alert {
  margin-bottom: 12px;
}

.movie-gallery {
  margin-top: 30px;
  padding: 0 20px;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.movie-gallery h3 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}

.movie-carousel {
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  padding: 8px 4px 14px;
  margin-bottom: 20px;
}

.movie-list {
  display: inline-flex;
  gap: 18px;
}

.movie-item {
  width: 220px;
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 18px;
  background: #f8fafc;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all .2s ease;
}

.movie-item.active {
  border-color: rgba(239, 68, 68, 0.35);
  background: rgba(239, 68, 68, 0.06);
}

.movie-poster {
  width: 84px;
  height: 118px;
  border-radius: 12px;
  flex-shrink: 0;
  background: #f5f7fa;
}

.movie-item-body {
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.movie-name {
  display: block;
  max-width: 100%;
}

.movie-item-meta {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.movie-price {
  color: #ef4238;
  font-size: 14px;
  font-weight: 600;
}

.movie-next-time {
  margin-top: 8px;
  color: #6b7280;
  font-size: 12px;
}

.room-section {
  margin-top: 6px;
}

.room-alert {
  margin-bottom: 16px;
}
</style>
