<template>
  <div class="movie-list-page">
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <el-card class="filter-card" shadow="never">
        <div class="header-row">
          <div>
            <div class="page-title">电影列表</div>
            <div class="page-subtitle">年份筛选已移除，仅保留真正可用的类型和区域筛选项。</div>
          </div>
        </div>

        <div class="search-bar">
          <el-input v-model="searchForm.name" placeholder="请输入你感兴趣的电影" @keyup.enter="search">
            <template #append>
              <el-button class="search-btn" @click="search">搜索</el-button>
            </template>
          </el-input>
        </div>

        <el-form label-width="60px" class="filter-form">
          <el-form-item label="类型">
            <el-radio-group v-model="searchForm.movieTypeId" @change="search">
              <el-radio-button v-for="item in movieTypeList" :key="`type-${item.value}`" :label="item.value">
                {{ item.label }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="区域">
            <el-radio-group v-model="searchForm.movieRegionId" @change="search">
              <el-radio-button v-for="item in movieRegionList" :key="`region-${item.value}`" :label="item.value">
                {{ item.label }}
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="result-card" shadow="never">
        <div class="result-header">
          <div>
            <div class="result-title">共找到 {{ pageInfo.total }} 部电影</div>
            <div class="result-subtitle">支持直接购票的影片显示红/蓝按钮，即将上映影片显示黄色“想看”。</div>
          </div>
          <el-button @click="resetSearch">重置筛选</el-button>
        </div>

        <el-row :gutter="18" v-if="listData.length > 0">
          <el-col :span="6" v-for="item in listData" :key="item.id" class="movie-col">
            <MovieCard :info="item" :mode="getCardMode(item)" />
          </el-col>
        </el-row>
        <el-empty v-else description="暂无匹配影片" />

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
  </div>
</template>

<script setup>
import request from '@/utils/http.js';
import {computed, ref, toRaw} from 'vue';
import MovieCard from '@/components/MovieCard.vue';
import {useRoute} from 'vue-router';
import {isUpcomingMovie} from '@/utils/movie.js';

const listData = ref([]);
const movieBaseList = ref([]);
const rawMovieTypeList = ref([]);
const rawMovieRegionList = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 12,
  total: 0
});
const searchForm = ref({
  name: '',
  movieTypeId: '',
  movieRegionId: ''
});

const route = useRoute();
if (route.query.name) {
  searchForm.value.name = route.query.name;
}

const movieTypeList = computed(() => buildFilterOptions(rawMovieTypeList.value, 'movieTypeId'));
const movieRegionList = computed(() => buildFilterOptions(rawMovieRegionList.value, 'movieRegionId'));

init();

function init() {
  getPageList();
  loadFilterOptions();
}

function getPageList() {
  const data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value));
  request.get('/movie/page', {params: data}).then(res => {
    listData.value = res.data.list || [];
    pageInfo.value.total = res.data.total || 0;
  });
}

function loadFilterOptions() {
  Promise.all([
    request.get('/movie/list'),
    request.get('/movieType/list'),
    request.get('/movieRegion/list')
  ]).then(([movieRes, typeRes, regionRes]) => {
    movieBaseList.value = Array.isArray(movieRes.data) ? movieRes.data : [];
    rawMovieTypeList.value = Array.isArray(typeRes.data) ? typeRes.data : [];
    rawMovieRegionList.value = Array.isArray(regionRes.data) ? regionRes.data : [];
  });
}

function buildFilterOptions(source, field) {
  const countMap = collectCountMap(field);
  const options = (Array.isArray(source) ? source : [])
    .map(item => ({
      label: item.name,
      value: item.id,
      count: countMap.get(normalizeId(item.id)) || 0
    }))
    .filter(item => item.count > 0)
    .map(item => ({
      label: `${item.label}${item.count ? ` ${item.count}` : ''}`,
      value: item.value
    }));
  return [{label: '全部', value: ''}, ...options];
}

function collectCountMap(field) {
  const counter = new Map();
  movieBaseList.value.forEach(item => {
    const key = normalizeId(item?.[field]);
    if (!key) {
      return;
    }
    counter.set(key, (counter.get(key) || 0) + 1);
  });
  return counter;
}

function normalizeId(value) {
  if (value === null || value === undefined || value === '') {
    return '';
  }
  return String(value);
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

function search() {
  pageInfo.value.pageNum = 1;
  getPageList();
}

function resetSearch() {
  searchForm.value = {name: '', movieTypeId: '', movieRegionId: ''};
  pageInfo.value.pageNum = 1;
  getPageList();
}

function getCardMode(item) {
  return isUpcomingMovie(item) ? 'upcoming' : 'hot';
}
</script>

<style scoped>
.movie-list-page {
  width: 74%;
  margin: 20px auto 0;
}

.filter-card,
.result-card {
  border-radius: 22px;
}

.header-row,
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-title,
.result-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.page-subtitle,
.result-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #6b7280;
}

.search-bar {
  margin: 18px 0 16px;
}

.search-btn {
  background: #ef4444;
  color: #fff;
  border-color: #ef4444;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 12px;
}

.filter-form :deep(.el-radio-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.movie-col {
  margin-bottom: 18px;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
