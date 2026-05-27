<template>
  <div>
    <div class="list-grid" v-if="listData.length > 0">
      <el-card v-for="item in listData" :key="item.id" class="history-card" shadow="hover">
        <div class="card-content" @click="router.push('/movieDetails/' + item.movieId)">
          <el-image :src="item.movieMainImg" class="movie-cover" fit="cover" />
          <div class="movie-info">
            <div class="movie-name">{{ item.movieName }}</div>
            <div class="movie-time">最近浏览：{{ item.createTime }}</div>
          </div>
        </div>

        <div class="card-actions">
          <el-button type="danger" plain @click.stop="removeItem(item)">删除记录</el-button>
        </div>
      </el-card>
    </div>
    <el-empty v-else description="暂无浏览历史" />

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
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
import router from '@/router/index.js';

const listData = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 8,
  total: 0
});

getPageList();

function getPageList() {
  request.get('/movieBrowsingHistory/page', {
    params: pageInfo.value
  }).then(res => {
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

function removeItem(item) {
  request.delete('/movieBrowsingHistory/delBatch', {data: [item.id]}).then(() => {
    ElMessage.success('浏览记录已删除');
    if (listData.value.length === 1 && pageInfo.value.pageNum > 1) {
      pageInfo.value.pageNum -= 1;
    }
    getPageList();
  });
}
</script>

<style scoped>
.list-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.history-card {
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

.movie-time {
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
