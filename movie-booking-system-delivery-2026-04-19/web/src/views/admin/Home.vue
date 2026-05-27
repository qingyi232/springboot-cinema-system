<template>
  <div class="admin-home">
    <el-space direction="vertical" alignment="left" style="width: 100%" size="large">
      <section class="hero-card">
        <div>
          <p class="hero-eyebrow">{{ heroEyebrow }}</p>
          <h2 class="hero-title">{{ heroTitle }}</h2>
          <p class="hero-desc">{{ heroDesc }}</p>
        </div>
        <div class="hero-meta">
          <el-tag type="primary" effect="dark" round>{{ currentUser?.type || 'ADMIN' }}</el-tag>
          <span>{{ currentUser?.nickname || currentUser?.username || '后台用户' }}</span>
        </div>
      </section>

      <el-row :gutter="20" class="summary-row">
        <el-col :xs="24" :sm="12" :lg="8">
          <div class="summary-card summary-card--primary">
            <div class="summary-label">{{ summaryCards[0].label }}</div>
            <div class="summary-value">{{ summaryCards[0].value }}</div>
            <div class="summary-desc">{{ summaryCards[0].desc }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="8">
          <div class="summary-card summary-card--success">
            <div class="summary-label">{{ summaryCards[1].label }}</div>
            <div class="summary-value">{{ summaryCards[1].value }}</div>
            <div class="summary-desc">{{ summaryCards[1].desc }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="24" :lg="8">
          <div class="summary-card summary-card--warning">
            <div class="summary-label">{{ summaryCards[2].label }}</div>
            <div class="summary-value">{{ summaryCards[2].value }}</div>
            <div class="summary-desc">{{ summaryCards[2].desc }}</div>
          </div>
        </el-col>
      </el-row>

      <el-card shadow="never" class="dashboard-card dashboard-card--wide">
        <MovieSalesTotalAmountChart />
      </el-card>

      <el-row :gutter="20">
        <el-col :xs="24" :lg="8">
          <el-card shadow="never" class="dashboard-card">
            <MovieTypeProportionOfChart />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card shadow="never" class="dashboard-card">
            <MovieRegionProportionOfChart />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card shadow="never" class="dashboard-card">
            <MovieYearsProportionOfChart />
          </el-card>
        </el-col>
      </el-row>
    </el-space>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import MovieSalesTotalAmountChart from "@/views/admin/chat/MovieSalesTotalAmountChart.vue";
import MovieTypeProportionOfChart from "@/views/admin/chat/MovieTypeProportionOfChart.vue";
import MovieRegionProportionOfChart from "@/views/admin/chat/MovieRegionProportionOfChart.vue";
import MovieYearsProportionOfChart from "@/views/admin/chat/MovieYearsProportionOfChart.vue";
import tools from "@/utils/tools.js";

const currentUser = ref(tools.getCurrentUser())
const isAdmin = computed(() => currentUser.value?.type === 'ADMIN')

const heroEyebrow = computed(() => isAdmin.value ? '管理员中心' : '影院管理中心')
const heroTitle = computed(() => '电影院管理系统')
const heroDesc = computed(() => isAdmin.value
  ? '统一查看售额趋势、影片结构和区域分布，方便运营、排片和内容管理使用同一套信息口径。'
  : '统一查看当前影院的排片、订单、评价与核销信息，让影院端和管理端保持同一套业务表达。'
)

const summaryCards = computed(() => {
  if (isAdmin.value) {
    return [
      {
        label: '销售看板',
        value: '趋势 + 对比',
        desc: '聚焦最近 3/7/14/30 天票房变化'
      },
      {
        label: '内容结构',
        value: '类型 / 口碑',
        desc: '辅助判断影片类型结构与评价反馈是否均衡'
      },
      {
        label: '区域热度',
        value: '区域占比',
        desc: '方便快速查看区域片源与受欢迎方向'
      }
    ]
  }

  return [
    {
      label: '放映管理',
      value: '影厅 / 排片',
      desc: '快速查看当前影院影厅与排片维护入口'
    },
    {
      label: '订单管理',
      value: '电影 / 卖品',
      desc: '统一处理电影订单、卖品订单与评价反馈'
    },
    {
      label: '验票核销',
      value: '票码核销',
      desc: '核销完成后订单状态与票房数据会同步更新'
    }
  ]
})
</script>

<style scoped>
.admin-home {
  min-height: 100%;
}
.hero-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  border-radius: 24px;
  background: linear-gradient(135deg, #111827 0%, #1d4ed8 100%);
  color: #fff;
}
.hero-eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  letter-spacing: .08em;
  text-transform: uppercase;
  opacity: .75;
}
.hero-title {
  margin: 0;
  font-size: 28px;
  line-height: 1.2;
}
.hero-desc {
  margin: 12px 0 0;
  max-width: 720px;
  color: rgba(255,255,255,.8);
}
.hero-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255,255,255,.9);
  white-space: nowrap;
}
.summary-row {
  width: 100%;
}
.summary-card {
  padding: 22px;
  border-radius: 20px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(15, 23, 42, .06);
}
.summary-card--primary { border: 1px solid rgba(37, 99, 235, .14); }
.summary-card--success { border: 1px solid rgba(5, 150, 105, .14); }
.summary-card--warning { border: 1px solid rgba(245, 158, 11, .18); }
.summary-label { font-size: 13px; color: #6b7280; }
.summary-value { margin-top: 10px; font-size: 24px; font-weight: 700; color: #111827; }
.summary-desc { margin-top: 8px; color: #909399; font-size: 13px; line-height: 1.6; }
.dashboard-card {
  border: none;
  border-radius: 24px;
}
.dashboard-card :deep(.el-card__body) {
  padding: 24px;
}
.dashboard-card--wide :deep(.el-card__body) {
  padding: 24px 24px 12px;
}
@media (max-width: 991px) {
  .hero-card {
    flex-direction: column;
  }
  .hero-meta {
    white-space: normal;
  }
}
</style>
