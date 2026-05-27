<template>
  <div class="personal-page" :key="activeName">
    <el-card class="personal-card" shadow="never">
      <template #header>
        <div class="header-row">
          <div>
            <div class="page-title">个人中心</div>
            <div class="page-subtitle">这里保留“想看电影”和“浏览历史”，卖品订单已并入“我的订单”。</div>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeName" tab-position="left">
        <el-tab-pane label="想看电影" name="movieCollect">
          <MovieCollect v-if="activeName === 'movieCollect'" />
        </el-tab-pane>
        <el-tab-pane label="电影浏览历史" name="movieBrowsingHistory">
          <MovieBrowsingHistory v-if="activeName === 'movieBrowsingHistory'" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useRoute} from 'vue-router';
import MovieCollect from '@/views/front/personalCenter/MovieCollect.vue';
import MovieBrowsingHistory from '@/views/front/personalCenter/MovieBrowsingHistory.vue';

const activeName = ref('movieCollect');
const route = useRoute();
if (route.query.type === 'movieBrowsingHistory' || route.query.type === 'movieCollect') {
  activeName.value = route.query.type;
}
</script>

<style scoped>
.personal-page {
  width: 74%;
  margin: 20px auto 0;
}

.personal-card {
  border-radius: 22px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.page-subtitle {
  margin-top: 6px;
  color: #6b7280;
  font-size: 13px;
}
</style>
