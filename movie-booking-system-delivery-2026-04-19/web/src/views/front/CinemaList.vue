<template>
  <div class="cinema-list-page">
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <el-card class="header-card" shadow="never">
        <div class="header-row">
          <div>
            <div class="page-title">电影院</div>
            <div class="page-subtitle">统一按客户口径展示影院名称、电话、地址和标签，不再显示邮箱字段。</div>
          </div>
        </div>
      </el-card>

      <el-row :gutter="20">
        <el-col
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          v-for="item in listData"
          :key="item.id"
          class="cinema-card-col"
        >
          <el-card class="cinema-card" shadow="hover" @click="router.push('/cinemaDetails/' + item.id)">
            <div class="cinema-card-header">
              <div class="cinema-avatar">
                <el-image
                  v-if="item.avatarUrl"
                  :src="item.avatarUrl"
                  fit="cover"
                  :preview-src-list="[item.avatarUrl]"
                  :preview-teleported="true"
                />
                <div v-else class="avatar-placeholder">
                  <el-icon><User /></el-icon>
                </div>
              </div>
              <div class="cinema-info">
                <h3 class="cinema-name">{{ item.nickname }}</h3>
                <div class="cinema-desc">点击查看可售电影、影厅和场次</div>
              </div>
            </div>

            <div class="cinema-card-content">
              <div class="info-item">
                <el-icon><Phone /></el-icon>
                <span>{{ item.tel || '暂无电话' }}</span>
              </div>
              <div class="info-item">
                <el-icon><Location /></el-icon>
                <span>{{ item.address || '暂无地址' }}</span>
              </div>
              <div class="info-item info-item--labels">
                <el-icon><PriceTag /></el-icon>
                <el-space wrap>
                  <template v-if="getLabels(item).length">
                    <el-tag
                      v-for="label in getLabels(item)"
                      :key="`${item.id}-${label}`"
                      type="primary"
                      effect="plain"
                    >
                      {{ label }}
                    </el-tag>
                  </template>
                  <el-tag v-else type="info" effect="plain">暂无标签</el-tag>
                </el-space>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-space>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {User, Phone, Location, PriceTag} from '@element-plus/icons-vue';
import {ref} from "vue";
import router from "@/router/index.js";

const listData = ref([]);

getPageList();

function getPageList() {
  request.get("/cinema/list").then(res => {
    listData.value = (res.data || []).filter(item => item?.status !== '禁用');
  });
}

function getLabels(item) {
  return String(item?.label || '')
    .split(',')
    .map(label => label.trim())
    .filter(Boolean);
}
</script>

<style scoped>
.cinema-list-page {
  width: 75%;
  margin: 20px auto 0;
}

.header-card {
  border-radius: 20px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
}

.page-subtitle {
  margin-top: 8px;
  color: #6b7280;
  font-size: 14px;
}

.cinema-card-col {
  margin-bottom: 20px;
}

.cinema-card {
  height: 100%;
  border-radius: 18px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.cinema-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.12);
}

.cinema-card-header {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.cinema-avatar {
  width: 80px;
  height: 80px;
  border-radius: 18px;
  overflow: hidden;
  margin-right: 15px;
  background-color: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cinema-avatar .el-image {
  width: 100%;
  height: 100%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 30px;
  color: #909399;
}

.cinema-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.cinema-name {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: #111827;
}

.cinema-desc {
  margin-top: 8px;
  color: #6b7280;
  font-size: 13px;
}

.cinema-card-content {
  padding: 15px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  color: #606266;
  gap: 8px;
}

.info-item--labels {
  margin-bottom: 0;
}

.info-item .el-icon {
  margin-top: 2px;
  font-size: 16px;
  color: #409EFF;
}

.info-item span {
  line-height: 1.7;
}
</style>
