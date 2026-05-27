<template>
  <div class="chart-panel">
    <div class="chart-header">
      <div>
        <div class="chart-title">电影年份占比</div>
        <div class="chart-subtitle">快速掌握新片与经典片的内容构成比例。</div>
      </div>
    </div>
    <div ref="chartRef" class="chart-surface chart-surface--pie"></div>
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import http from "@/utils/http.js";

const chartRef = ref(null)
let chartInstance = null

const option = {
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, left: 'center', icon: 'circle' },
  series: [
    {
      name: '数量',
      type: 'pie',
      radius: ['42%', '68%'],
      center: ['50%', '44%'],
      data: [],
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      emphasis: { itemStyle: { shadowBlur: 14, shadowColor: 'rgba(15, 23, 42, .18)' } },
      label: { formatter: params => `${params.name}\n${params.percent}%` }
    }
  ]
}

function ensureChart() {
  if (!chartRef.value) return null
  if (!chartInstance) chartInstance = echarts.init(chartRef.value)
  return chartInstance
}

function resizeChart() {
  chartInstance?.resize()
}

function renderChart(data) {
  const chart = ensureChart()
  if (!chart) return
  option.series[0].data = data || []
  chart.setOption(option, true)
  resizeChart()
}

function getData() {
  http.get('/statisticalReportForms/movieYearsProportionOfChar').then(res => {
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
.chart-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 18px; }
.chart-title { font-size: 18px; font-weight: 700; color: #111827; }
.chart-subtitle { margin-top: 6px; font-size: 13px; color: #909399; line-height: 1.5; }
.chart-surface { width: 100%; min-width: 0; }
.chart-surface--pie { height: 360px; }
</style>
