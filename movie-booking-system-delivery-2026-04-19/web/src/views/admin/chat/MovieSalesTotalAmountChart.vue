<template>
  <div class="chart-panel">
    <div class="chart-header">
      <div>
        <div class="chart-title">销售额趋势</div>
        <div class="chart-subtitle">按时间维度查看订单销售变化，方便运营判断活动与排片效果。</div>
      </div>
      <el-radio-group v-model="day" @change="getData" class="range-switcher">
        <el-radio-button :label="3">3天</el-radio-button>
        <el-radio-button :label="7">7天</el-radio-button>
        <el-radio-button :label="14">14天</el-radio-button>
        <el-radio-button :label="30">30天</el-radio-button>
      </el-radio-group>
    </div>
    <div ref="chartRef" class="chart-surface"></div>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import http from "@/utils/http.js";

const day = ref(7)
const chartRef = ref(null)
let chartInstance = null

const option = {
  color: ['#2563eb', '#f59e0b'],
  tooltip: { trigger: 'axis' },
  legend: { top: 0, right: 0, data: ['销售额', '趋势线'] },
  grid: { left: 24, right: 24, bottom: 20, top: 52, containLabel: true },
  xAxis: {
    type: 'category',
    data: [],
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#d0d5dd' } }
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: { lineStyle: { type: 'dashed', color: '#e5e7eb' } }
  },
  series: [
    {
      name: '销售额',
      type: 'bar',
      barMaxWidth: 32,
      data: [],
      itemStyle: { borderRadius: [8, 8, 0, 0] }
    },
    {
      name: '趋势线',
      type: 'line',
      smooth: true,
      data: [],
      symbolSize: 8,
      lineStyle: { width: 3 }
    }
  ]
}

function ensureChart() {
  if (!chartRef.value) return null
  if (!chartInstance) chartInstance = echarts.init(chartRef.value)
  return chartInstance
}
function resizeChart() { chartInstance?.resize() }
function renderChart(data) {
  const chart = ensureChart()
  if (!chart) return
  option.xAxis.data = data?.xData || []
  option.series[0].data = data?.seriesData || []
  option.series[1].data = data?.seriesData || []
  chart.setOption(option, true)
  resizeChart()
}
function getData() {
  http.get(`/statisticalReportForms/movieSalesTotalAmountChart/${day.value}`).then(res => {
    renderChart(res.data)
  })
}

onMounted(async () => {
  await nextTick()
  getData()
  window.addEventListener('resize', resizeChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.chart-panel { width: 100%; }
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
}
.chart-title { font-size: 18px; font-weight: 700; color: #111827; }
.chart-subtitle { margin-top: 6px; font-size: 13px; color: #909399; line-height: 1.5; }
.chart-surface { width: 100%; height: 360px; }
.range-switcher { flex-wrap: wrap; }
@media (max-width: 768px) {
  .chart-header { flex-direction: column; }
}
</style>
