<template>
  <section>
    <div class="page-title-row">
      <div>
        <h1>数据可视化分析</h1>
        <div class="muted-text">当前记录数：{{ filteredMeasurements.length }}</div>
      </div>
      <div class="toolbar">
        <el-select v-model="selectedPenNo" placeholder="选择栏位" class="pen-select">
          <el-option label="全部栏位" value="all" />
          <el-option v-for="penNo in penOptions" :key="penNo" :label="penNo" :value="penNo" />
        </el-select>
        <el-button :loading="loading" :icon="RefreshRight" @click="loadMeasurements">刷新</el-button>
      </div>
    </div>

    <div class="filter-panel page-panel">
      <div class="filter-item">
        <span class="filter-label">栏位</span>
        <strong>{{ selectedPenNo === 'all' ? '全部栏位' : selectedPenNo }}</strong>
      </div>
      <div class="filter-item">
        <span class="filter-label">记录</span>
        <strong>{{ filteredMeasurements.length }}</strong>
      </div>
      <div class="filter-item">
        <span class="filter-label">平均体长</span>
        <strong>{{ averageMap.bodyLength }}</strong>
      </div>
      <div class="filter-item">
        <span class="filter-label">平均胸围</span>
        <strong>{{ averageMap.chestGirth }}</strong>
      </div>
    </div>

    <el-empty v-if="!loading && !filteredMeasurements.length" description="暂无可视化数据">
      <el-button type="primary" @click="loadMeasurements">重新加载</el-button>
    </el-empty>

    <div v-else class="chart-grid">
      <div class="page-panel chart-panel chart-panel-wide">
        <div class="chart-header">
          <h2>各指标平均值</h2>
          <el-tag effect="plain" type="success">平均值</el-tag>
        </div>
        <div :ref="el => setChartRef('average', el)" class="chart-box"></div>
      </div>

      <div v-for="metric in metricConfigs" :key="metric.key" class="page-panel chart-panel">
        <div class="chart-header">
          <h2>{{ metric.label }}指标图</h2>
          <el-tag effect="plain">{{ metric.label }}</el-tag>
        </div>
        <div :ref="el => setChartRef(metric.key, el)" class="chart-box"></div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch, type ComponentPublicInstance } from 'vue';
import { ElMessage } from 'element-plus';
import { RefreshRight } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import { fetchMeasurements } from '@/api/measurement';
import type { BodyMeasurement } from '@/types/measurement';

type MetricKey = 'bodyLength' | 'bodyHeight' | 'chestWidth' | 'chestDepth' | 'chestGirth';
type ChartKey = MetricKey | 'average';

interface MetricConfig {
  key: MetricKey;
  label: string;
  color: string;
  chartType: 'line' | 'area' | 'heatmap' | 'bar' | 'stepLine';
}

const metricConfigs: MetricConfig[] = [
  { key: 'bodyLength', label: '体长', color: '#2563eb', chartType: 'line' },
  { key: 'bodyHeight', label: '体高', color: '#0ea5e9', chartType: 'area' },
  { key: 'chestWidth', label: '胸宽', color: '#14b8a6', chartType: 'heatmap' },
  { key: 'chestDepth', label: '胸深', color: '#6366f1', chartType: 'bar' },
  { key: 'chestGirth', label: '胸围', color: '#0891b2', chartType: 'stepLine' }
];

const measurements = ref<BodyMeasurement[]>([]);
const loading = ref(false);
const selectedPenNo = ref('all');
const chartRefs: Partial<Record<ChartKey, HTMLDivElement>> = {};
const chartInstances: Partial<Record<ChartKey, echarts.ECharts>> = {};

const penOptions = computed(() => {
  const penSet = new Set(measurements.value.map(item => item.penNo).filter(Boolean));
  return Array.from(penSet).sort((left, right) => left.localeCompare(right, 'zh-Hans-CN', { numeric: true }));
});

const filteredMeasurements = computed(() => {
  const rows = selectedPenNo.value === 'all'
    ? measurements.value
    : measurements.value.filter(item => item.penNo === selectedPenNo.value);

  return [...rows].sort((left, right) => Number(left.id || 0) - Number(right.id || 0));
});

const averageMap = computed(() => {
  return metricConfigs.reduce<Record<MetricKey, string>>((map, metric) => {
    map[metric.key] = formatAverage(filteredMeasurements.value.map(row => Number(row[metric.key] || 0)));
    return map;
  }, {
    bodyLength: '--',
    bodyHeight: '--',
    chestWidth: '--',
    chestDepth: '--',
    chestGirth: '--'
  });
});

onMounted(() => {
  loadMeasurements();
  window.addEventListener('resize', resizeCharts);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts);
  Object.values(chartInstances).forEach(chart => chart?.dispose());
});

watch(selectedPenNo, async () => {
  await nextTick();
  renderCharts();
});

async function loadMeasurements() {
  loading.value = true;
  try {
    const response = await fetchMeasurements();
    measurements.value = response.data.data || [];
    if (selectedPenNo.value !== 'all' && !penOptions.value.includes(selectedPenNo.value)) {
      selectedPenNo.value = 'all';
    }
    await nextTick();
    renderCharts();
  } catch {
    ElMessage.error('数据加载失败');
  } finally {
    loading.value = false;
  }
}

function setChartRef(key: ChartKey, el: Element | ComponentPublicInstance | null) {
  if (el instanceof HTMLDivElement) {
    chartRefs[key] = el;
  }
}

function renderCharts() {
  if (!filteredMeasurements.value.length) {
    return;
  }

  metricConfigs.forEach(renderMetricChart);
  renderAverageChart();
}

function renderMetricChart(metric: MetricConfig) {
  const chart = getChart(metric.key);
  if (!chart) {
    return;
  }

  const data = filteredMeasurements.value.map(row => Number(row[metric.key] || 0));
  const sequenceLabels = filteredMeasurements.value.map((_, index) => String(index + 1));
  const isHeatmap = metric.chartType === 'heatmap';
  const maxValue = Math.max(...data, 1);

  chart.setOption({
    color: [metric.color],
    tooltip: {
      trigger: isHeatmap ? 'item' : 'axis',
      backgroundColor: '#ffffff',
      borderColor: '#e5e7eb',
      textStyle: { color: '#1f2937' },
      formatter: isHeatmap
        ? (params: { value?: unknown }) => {
            const value = Array.isArray(params.value) ? params.value : [];
            const sequence = Number(value[0]) + 1 || '--';
            return `序号：${sequence}<br/>${metric.label}：${value[2] || 0}`;
          }
        : undefined
    },
    grid: { left: 52, right: 24, top: 30, bottom: 46 },
    xAxis: {
      type: 'category',
      name: '序号',
      nameLocation: 'middle',
      nameGap: 30,
      data: sequenceLabels,
      axisLabel: { color: '#6b7280', fontSize: 11 },
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e5e7eb' } }
    },
    yAxis: isHeatmap
      ? {
          type: 'category',
          data: [metric.label],
          axisLabel: { color: '#6b7280', fontSize: 12 },
          axisTick: { show: false },
          axisLine: { lineStyle: { color: '#e5e7eb' } }
        }
      : {
          type: 'value',
          name: metric.label,
          nameTextStyle: { color: '#6b7280' },
          splitLine: { lineStyle: { color: '#eef2f7' } }
        },
    visualMap: isHeatmap
      ? {
          show: false,
          min: 0,
          max: maxValue,
          inRange: {
            color: ['#ecfeff', '#bae6fd', metric.color]
          }
        }
      : undefined,
    series: [
      buildMetricSeries(metric, data)
    ]
  }, true);
}

function buildMetricSeries(metric: MetricConfig, data: number[]) {
  const baseLabel = {
    show: true,
    position: 'top',
    color: '#4b5563',
    fontSize: 11
  };

  if (metric.chartType === 'line') {
    return {
      name: metric.label,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data,
      lineStyle: { width: 3 },
      itemStyle: { borderColor: '#ffffff', borderWidth: 2 },
      label: baseLabel
    };
  }

  if (metric.chartType === 'area') {
    return {
      name: metric.label,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 7,
      data,
      lineStyle: { width: 3 },
      areaStyle: {
        opacity: 0.22,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: `${metric.color}66` },
          { offset: 1, color: `${metric.color}08` }
        ])
      },
      label: baseLabel
    };
  }

  if (metric.chartType === 'heatmap') {
    return {
      name: metric.label,
      type: 'heatmap',
      data: data.map((value, index) => [index, 0, value]),
      itemStyle: {
        borderRadius: 6,
        borderColor: '#ffffff',
        borderWidth: 3
      },
      label: {
        show: true,
        color: '#0f172a',
        fontSize: 12,
        formatter: (params: { value?: unknown }) => {
          const value = Array.isArray(params.value) ? params.value : [];
          return value[2] || '';
        }
      }
    };
  }

  if (metric.chartType === 'stepLine') {
    return {
      name: metric.label,
      type: 'line',
      step: 'middle',
      symbol: 'diamond',
      symbolSize: 8,
      data,
      lineStyle: { width: 3 },
      itemStyle: { borderColor: '#ffffff', borderWidth: 2 },
      label: baseLabel
    };
  }

  return {
    name: metric.label,
    type: 'bar',
    barMaxWidth: 38,
    data,
    itemStyle: {
      borderRadius: [6, 6, 0, 0],
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: metric.color },
        { offset: 1, color: `${metric.color}99` }
      ])
    },
    label: baseLabel
  };
}

function renderAverageChart() {
  const chart = getChart('average');
  if (!chart) {
    return;
  }

  chart.setOption({
    color: ['#2563eb', '#0ea5e9', '#14b8a6', '#6366f1', '#0891b2'],
    tooltip: {
      backgroundColor: '#ffffff',
      borderColor: '#e5e7eb',
      textStyle: { color: '#1f2937' }
    },
    radar: {
      center: ['50%', '52%'],
      radius: '68%',
      indicator: metricConfigs.map(metric => ({
        name: metric.label,
        max: Math.max(Number(averageMap.value[metric.key] === '--' ? 0 : averageMap.value[metric.key]) * 1.35, 1)
      })),
      axisName: { color: '#4b5563' },
      axisLine: { lineStyle: { color: '#dbeafe' } },
      splitLine: { lineStyle: { color: '#dbeafe' } },
      splitArea: {
        areaStyle: { color: ['#ffffff', '#f0f9ff'] }
      }
    },
    series: [
      {
        name: '平均值',
        type: 'radar',
        data: [
          {
            name: selectedPenNo.value === 'all' ? '全部栏位平均值' : `${selectedPenNo.value} 平均值`,
            value: metricConfigs.map(metric => Number(averageMap.value[metric.key] === '--' ? 0 : averageMap.value[metric.key])),
            areaStyle: {
              opacity: 0.22,
              color: '#0ea5e9'
            },
            lineStyle: {
              color: '#2563eb',
              width: 3
            },
            itemStyle: {
              color: '#2563eb'
            }
          }
        ]
      }
    ]
  }, true);
}

function getChart(key: ChartKey) {
  const element = chartRefs[key];
  if (!element) {
    return null;
  }

  if (!chartInstances[key]) {
    chartInstances[key] = echarts.init(element);
  }

  return chartInstances[key] || null;
}

function resizeCharts() {
  Object.values(chartInstances).forEach(chart => chart?.resize());
}

function formatAverage(values: number[]) {
  const validValues = values.filter(value => Number.isFinite(value) && value > 0);
  if (!validValues.length) {
    return '--';
  }

  const total = validValues.reduce((sum, value) => sum + value, 0);
  return (total / validValues.length).toFixed(1);
}
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pen-select {
  width: 180px;
}

.filter-panel {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1px;
  margin-bottom: 18px;
  overflow: hidden;
  background: var(--color-border);
}

.filter-item {
  min-height: 72px;
  padding: 16px;
  background: #fff;
}

.filter-label {
  display: block;
  margin-bottom: 6px;
  color: var(--color-muted);
  font-size: 13px;
}

.filter-item strong {
  color: #111827;
  font-size: 20px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.chart-panel {
  padding: 18px;
}

.chart-panel-wide {
  grid-column: 1 / -1;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.chart-header h2 {
  margin: 0;
  color: #111827;
  font-size: 16px;
  font-weight: 700;
}

.chart-box {
  width: 100%;
  height: 330px;
}

@media (max-width: 1100px) {
  .chart-grid,
  .filter-panel {
    grid-template-columns: 1fr;
  }

  .toolbar {
    align-items: stretch;
    flex-direction: column;
    width: 100%;
  }

  .pen-select {
    width: 100%;
  }
}
</style>
