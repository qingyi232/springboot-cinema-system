<template>
  <div>
    <div class="wish-grid" v-if="pageList.length > 0">
      <el-card v-for="item in pageList" :key="item.id" class="wish-card" shadow="hover">
        <div class="card-content" @click="router.push('/movieDetails/' + item.movieId)">
          <el-image :src="item.movieMainImg" class="movie-cover" fit="cover" />
          <div class="movie-info">
            <div class="movie-name">{{ item.movieName }}</div>
            <div class="movie-time">加入想看：{{ item.createTime }}</div>
            <div class="movie-release">上映日期：{{ item.releaseDate || '待定' }}</div>
          </div>
        </div>

        <div class="card-actions">
          <el-button type="warning" plain @click.stop="removeItem(item)">取消想看</el-button>
        </div>
      </el-card>
    </div>
    <el-empty v-else description="暂无想看电影" />

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
  </div>
</template>

<script setup>
import request from '@/utils/http.js';
import {computed, ref} from 'vue';
import {ElMessage} from 'element-plus';
import router from '@/router/index.js';
import {isUpcomingMovie} from '@/utils/movie.js';

const allList = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 8,
  total: 0
});

const pageList = computed(() => {
  const start = (pageInfo.value.pageNum - 1) * pageInfo.value.pageSize;
  const end = start + pageInfo.value.pageSize;
  return allList.value.slice(start, end);
});

loadAll();

function loadAll() {
  Promise.all([
    request.get('/movieCollect/page', {
      params: {
        pageNum: 1,
        pageSize: 1000
      }
    }),
    request.get('/movie/list')
  ]).then(([collectRes, movieRes]) => {
    const source = collectRes.data.list || [];
    const movieMap = new Map((movieRes.data || []).map(item => [item.id, item]));
    const detailList = source.map(item => {
      const movie = movieMap.get(item.movieId) || {};
      return {
        ...item,
        releaseDate: movie.releaseDate,
        lowestPrice: movie.lowestPrice
      };
    });
    allList.value = detailList.filter(item => isUpcomingMovie(item));
    pageInfo.value.total = allList.value.length;
    if (pageInfo.value.pageNum > Math.ceil(allList.value.length / pageInfo.value.pageSize) && pageInfo.value.pageNum > 1) {
      pageInfo.value.pageNum -= 1;
    }
  });
}

function currentChange(pageNum) {
  pageInfo.value.pageNum = pageNum;
}

function sizeChange(pageSize) {
  pageInfo.value.pageSize = pageSize;
  pageInfo.value.pageNum = 1;
}

function removeItem(item) {
  request.delete('/movieCollect/delBatch', {data: [item.id]}).then(() => {
    ElMessage.success('已取消想看');
    loadAll();
  });
}
</script>

<style scoped>
.wish-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.wish-card {
  border-radius: 18px;
}

.card-content {
  display: flex;
  gap: 14px;
  cursor: pointer;
}

.movie-cover {
  width: 110px;
  height: 148px;
  border-radius: 14px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.movie-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.movie-name {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}

.movie-time,
.movie-release {
  font-size: 13px;
  color: #6b7280;
}

.card-actions {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}

.pagination-wrap {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
}
</style>
