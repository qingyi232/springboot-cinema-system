import * as echarts from 'echarts'

const PIE_COLORS = ['#4f46e5', '#22c55e', '#f59e0b', '#ef4444', '#0ea5e9', '#8b5cf6', '#14b8a6', '#f97316']

function formatCurrency(value = 0) {
  const amount = Number(value || 0)
  return amount.toFixed(2)
}

export function sumSeries(series = []) {
  return series.reduce((sum, item) => sum + Number(item || 0), 0)
}

export function getPeakPoint(xData = [], seriesData = []) {
  if (!seriesData.length) {
    return { label: '暂无数据', value: 0 }
  }

  let peakIndex = 0
  seriesData.forEach((item, index) => {
    if (Number(item || 0) > Number(seriesData[peakIndex] || 0)) {
      peakIndex = index
    }
  })

  return {
    label: xData[peakIndex] || '暂无数据',
    value: Number(seriesData[peakIndex] || 0),
  }
}

export function getTopPieItem(data = []) {
  if (!data.length) {
    return { name: '暂无数据', value: 0 }
  }

  return data.reduce((top, item) => {
    return Number(item?.value || 0) > Number(top?.value || 0) ? item : top
  }, data[0])
}

export function buildTrendChartOption({ title, xData = [], seriesData = [] }) {
  return {
    color: ['#4f46e5', '#22c55e'],
    title: {
      text: title,
      left: 0,
      top: 0,
      textStyle: {
        color: '#0f172a',
        fontSize: 16,
        fontWeight: 600,
      },
    },
    tooltip: {
      trigger: 'axis',
      valueFormatter: (value) => `￥${formatCurrency(value)}`,
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderWidth: 0,
      textStyle: {
        color: '#fff',
      },
    },
    grid: {
      left: 44,
      right: 18,
      top: 54,
      bottom: 32,
    },
    xAxis: {
      type: 'category',
      boundaryGap: true,
      data: xData,
      axisTick: {
        show: false,
      },
      axisLine: {
        lineStyle: {
          color: '#cbd5e1',
        },
      },
      axisLabel: {
        color: '#64748b',
      },
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#64748b',
        formatter: (value) => `￥${value}`,
      },
      splitLine: {
        lineStyle: {
          color: '#e2e8f0',
          type: 'dashed',
        },
      },
    },
    series: [
      {
        name: '销售额',
        type: 'bar',
        barWidth: 18,
        data: seriesData,
        itemStyle: {
          borderRadius: [10, 10, 0, 0],
          color: '#818cf8',
        },
      },
      {
        name: '趋势线',
        type: 'line',
        data: seriesData,
        smooth: true,
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: '#22c55e',
        },
        itemStyle: {
          color: '#22c55e',
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(34, 197, 94, 0.25)' },
            { offset: 1, color: 'rgba(34, 197, 94, 0.02)' },
          ]),
        },
      },
    ],
  }
}

export function buildPieChartOption({ title, data = [], centerLabel = '' }) {
  return {
    color: PIE_COLORS,
    title: {
      text: title,
      left: 0,
      top: 0,
      textStyle: {
        color: '#0f172a',
        fontSize: 16,
        fontWeight: 600,
      },
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{c} 部 · {d}%',
      backgroundColor: 'rgba(15, 23, 42, 0.92)',
      borderWidth: 0,
      textStyle: {
        color: '#fff',
      },
    },
    legend: {
      bottom: 0,
      left: 'center',
      icon: 'circle',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        color: '#475569',
      },
    },
    graphic: centerLabel
      ? [
          {
            type: 'text',
            left: 'center',
            top: '36%',
            style: {
              text: centerLabel,
              textAlign: 'center',
              fill: '#0f172a',
              fontSize: 14,
              fontWeight: 600,
              lineHeight: 20,
            },
          },
        ]
      : [],
    series: [
      {
        name: title,
        type: 'pie',
        radius: ['48%', '72%'],
        center: ['50%', '44%'],
        avoidLabelOverlap: true,
        label: {
          color: '#334155',
          formatter: '{b}\n{d}%',
        },
        labelLine: {
          length: 12,
          length2: 8,
        },
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 3,
        },
        emphasis: {
          scale: true,
          itemStyle: {
            shadowBlur: 24,
            shadowColor: 'rgba(79, 70, 229, 0.18)',
          },
        },
        data,
      },
    ],
  }
}
