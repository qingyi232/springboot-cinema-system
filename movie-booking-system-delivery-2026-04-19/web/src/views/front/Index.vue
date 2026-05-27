<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-inner">
        <div class="hero-copy">
          <el-tag type="danger" effect="dark" round>在线选座购票</el-tag>
          <h1>热映、想看、猜你喜欢，一站式完成选座购票</h1>
          <p>首页已重构为热映中、即将上映、猜你喜欢三大分区；支持想看、影院选择、影厅场次、选座支付与订单追踪全流程。</p>
          <div class="hero-search">
            <el-input v-model="searchName" placeholder="搜索电影名称" @keyup.enter="search">
              <template #append>
                <el-button class="hero-search-btn" @click="search">搜索</el-button>
              </template>
            </el-input>
          </div>
          <div class="hero-actions">
            <el-button type="danger" round @click="openLink('/movieList')">去电影列表</el-button>
            <el-button round @click="openLink('/cinemaList')">找电影院</el-button>
          </div>
        </div>

        <div class="hero-carousel-wrap">
          <el-carousel height="400px" class="hero-carousel" indicator-position="outside">
            <el-carousel-item v-for="item in slideshow" :key="item.id || item.mainImg" @click="openLink(item.link)">
              <el-image class="hero-carousel-image" fit="cover" :src="item.mainImg" />
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>
    </section>

    <div class="content-wrap">
      <div class="section-card" v-for="section in sectionList" :key="section.key">
        <div class="section-header">
          <div class="section-title-wrap">
            <div class="section-icon" :class="`section-icon--${section.key}`">
              <el-icon><component :is="section.icon" /></el-icon>
            </div>
            <div>
              <div class="section-title">{{ section.title }}</div>
              <div class="section-subtitle">{{ section.subtitle }}</div>
            </div>
          </div>
          <el-button text :type="section.linkType" @click="openLink(section.link)">{{ section.moreText }}</el-button>
        </div>

        <el-row :gutter="18">
          <el-col :span="6" v-for="item in section.list" :key="`${section.key}-${item.id}`">
            <MovieCard :info="item" :mode="section.mode" />
          </el-col>
        </el-row>

        <el-empty v-if="!section.list.length" :description="`${section.title}暂无内容`" />
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed, ref} from 'vue';
import request from '@/utils/http.js';
import router from '@/router/index.js';
import MovieCard from '@/components/MovieCard.vue';
import {Clock, Star, VideoPlay} from '@element-plus/icons-vue';
import {isNowShowingMovie, isUpcomingMovie, sortByHotWeight, sortByUpcomingRelease} from '@/utils/movie.js';

const slideshow = ref([]);
const movieSource = ref([]);
const movieRecommend = ref([]);
const searchName = ref('');

const hotMovies = computed(() => {
  return movieSource.value
    .filter(item => isNowShowingMovie(item))
    .sort(sortByHotWeight)
    .slice(0, 4);
});

const upcomingMovies = computed(() => {
  return movieSource.value
    .filter(item => isUpcomingMovie(item))
    .sort(sortByUpcomingRelease)
    .slice(0, 4);
});

const guessMovies = computed(() => {
  const excludedIds = new Set([...hotMovies.value, ...upcomingMovies.value].map(item => item.id));
  const preferred = movieRecommend.value
    .filter(item => !excludedIds.has(item.id) && isNowShowingMovie(item));
  const fallback = movieSource.value
    .filter(item => !excludedIds.has(item.id) && isNowShowingMovie(item) && !preferred.find(movie => movie.id === item.id));
  return [...preferred, ...fallback].slice(0, 4);
});

const sectionList = computed(() => ([
  {
    key: 'hot',
    title: '热映中',
    subtitle: '正在上映，支持直接选座购票',
    list: hotMovies.value,
    mode: 'hot',
    icon: VideoPlay,
    link: '/movieList',
    moreText: '查看全部电影',
    linkType: 'danger'
  },
  {
    key: 'upcoming',
    title: '即将上映',
    subtitle: '仅展示真正即将上映的影片，支持加入或取消想看',
    list: upcomingMovies.value,
    mode: 'upcoming',
    icon: Clock,
    link: '/personalCenter?type=movieCollect',
    moreText: '去个人中心看想看',
    linkType: 'warning'
  },
  {
    key: 'guess',
    title: '猜你喜欢',
    subtitle: '优先推荐未出现在上方板块里的可购影片',
    list: guessMovies.value,
    mode: 'guess',
    icon: Star,
    link: '/movieList',
    moreText: '查看更多推荐',
    linkType: 'primary'
  }
]));

init();

function init() {
  getSlideshowList();
  getMovieList();
  getMovieRecommendList();
}

function getSlideshowList() {
  request.get('/slideshow/page', {
    params: {pageNum: 1, pageSize: 10}
  }).then(res => {
    slideshow.value = res.data.list || [];
  });
}

function getMovieList() {
  request.get('/movie/page', {
    params: {pageNum: 1, pageSize: 30}
  }).then(res => {
    movieSource.value = res.data.list || [];
  });
}

function getMovieRecommendList() {
  request.get('/movie/recommend/10').then(res => {
    movieRecommend.value = res.data || [];
  });
}

function search() {
  router.push(`/movieList?name=${encodeURIComponent(searchName.value || '')}`);
}

function openLink(link) {
  if (!link) {
    return;
  }
  if (/^https?:\/\//.test(link)) {
    window.location.href = link;
    return;
  }
  const finalLink = link.startsWith('/') ? link : `/${link}`;
  router.push(finalLink);
}
</script>

<style scoped>
.home-page {
  background: linear-gradient(180deg, #fff 0%, #f8fafc 100%);
  min-height: 100%;
}

.hero-section {
  background: radial-gradient(circle at left top, rgba(239, 68, 68, 0.1), transparent 34%), #fff;
  border-bottom: 1px solid #eef2f7;
}

.hero-inner,
.content-wrap {
  width: 74%;
  margin: 0 auto;
}

.hero-inner {
  display: grid;
  grid-template-columns: 1.05fr .95fr;
  gap: 24px;
  padding: 28px 0 30px;
  align-items: center;
}

.hero-copy h1 {
  margin: 16px 0 12px;
  font-size: 34px;
  line-height: 1.25;
  color: #111827;
}

.hero-copy p {
  margin: 0;
  font-size: 15px;
  line-height: 1.9;
  color: #4b5563;
}

.hero-search {
  margin-top: 24px;
  max-width: 540px;
}

.hero-search-btn {
  background: #ef4444;
  color: #fff;
  border-color: #ef4444;
}

.hero-actions {
  margin-top: 18px;
  display: flex;
  gap: 12px;
}

.hero-carousel-wrap {
  min-width: 0;
}

.hero-carousel {
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.12);
}

.hero-carousel-image {
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.content-wrap {
  padding: 24px 0 30px;
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.section-card {
  padding: 22px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid #eef2f7;
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.04);
}

.section-header {
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.section-title-wrap {
  display: flex;
  align-items: center;
  gap: 14px;
}

.section-icon {
  width: 46px;
  height: 46px;
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
}

.section-icon--hot {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.section-icon--upcoming {
  background: linear-gradient(135deg, #f59e0b, #facc15);
}

.section-icon--guess {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.section-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #6b7280;
}
</style>
