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
}

const metricConfigs: MetricConfig[] = [
  { key: 'bodyLength', label: '体长', color: '#2563eb' },
  { key: 'bodyHeight', label: '体高', color: '#0f8b6f' },
  { key: 'chestWidth', label: '胸宽', color: '#b7791f' },
  { key: 'chestDepth', label: '胸深', color: '#7c3aed' },
  { key: 'chestGirth', label: '胸围', color: '#0891b2' }
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

  chart.setOption({
    color: [metric.color],
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#ffffff',
      borderColor: '#e5e7eb',
      textStyle: { color: '#1f2937' }
    },
    grid: { left: 52, right: 24, top: 30, bottom: 46 },
    xAxis: {
      type: 'category',
      name: 'ID号',
      nameLocation: 'middle',
      nameGap: 30,
      data: filteredMeasurements.value.map(row => String(row.id)),
      axisLabel: { color: '#6b7280', fontSize: 11 },
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e5e7eb' } }
    },
    yAxis: {
      type: 'value',
      name: metric.label,
      nameTextStyle: { color: '#6b7280' },
      splitLine: { lineStyle: { color: '#eef2f7' } }
    },
    series: [
      {
        name: metric.label,
        type: 'bar',
        barMaxWidth: 38,
        data: filteredMeasurements.value.map(row => Number(row[metric.key] || 0)),
        itemStyle: {
          borderRadius: [6, 6, 0, 0]
        },
        label: {
          show: true,
          position: 'top',
          color: '#4b5563',
          fontSize: 11
        }
      }
    ]
  }, true);
}

function renderAverageChart() {
  const chart = getChart('average');
  if (!chart) {
    return;
  }

  chart.setOption({
    color: ['#2563eb'],
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#ffffff',
      borderColor: '#e5e7eb',
      textStyle: { color: '#1f2937' }
    },
    grid: { left: 52, right: 24, top: 30, bottom: 42 },
    xAxis: {
      type: 'category',
      data: metricConfigs.map(metric => metric.label),
      axisLabel: { color: '#6b7280', fontSize: 12 },
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e5e7eb' } }
    },
    yAxis: {
      type: 'value',
      name: '平均值',
      nameTextStyle: { color: '#6b7280' },
      splitLine: { lineStyle: { color: '#eef2f7' } }
    },
    series: [
      {
        name: '平均值',
        type: 'bar',
        barMaxWidth: 46,
        data: metricConfigs.map(metric => Number(averageMap.value[metric.key] === '--' ? 0 : averageMap.value[metric.key])),
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color(params: { dataIndex: number }) {
            return metricConfigs[params.dataIndex]?.color || '#2563eb';
          }
        },
        label: {
          show: true,
          position: 'top',
          color: '#4b5563'
        }
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
