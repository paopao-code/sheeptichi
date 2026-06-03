<template>
  <aside class="side-bar">
    <el-menu :default-active="activePath" router class="side-menu">
      <el-menu-item index="/data-display">
        <el-icon><Grid /></el-icon>
        <span>数据展示</span>
      </el-menu-item>
      <el-menu-item index="/visual-analysis">
        <el-icon><TrendCharts /></el-icon>
        <span>数据可视化分析</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { Grid, TrendCharts } from '@element-plus/icons-vue';

const route = useRoute();
const activePath = computed(() => route.path);
</script>

<style scoped>
.side-bar {
  width: 228px;
  flex: 0 0 228px;
  background: #fff;
  border-right: 1px solid var(--color-border);
}

.side-menu {
  min-height: 100%;
  padding: 14px 10px;
  border-right: none;
}

:deep(.el-menu-item) {
  height: 44px;
  margin-bottom: 6px;
  border-radius: var(--radius);
  color: #4b5563;
}

:deep(.el-menu-item:hover) {
  color: var(--color-primary);
  background: var(--color-primary-soft);
}

:deep(.el-menu-item.is-active) {
  color: var(--color-primary);
  background: var(--color-primary-soft);
  font-weight: 700;
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

@media (max-width: 768px) {
  .side-bar {
    width: 100%;
    flex: none;
    border-right: none;
    border-bottom: 1px solid var(--color-border);
  }

  .side-menu {
    display: flex;
    min-height: auto;
    padding: 8px;
  }

  :deep(.el-menu-item) {
    flex: 1;
    justify-content: center;
    margin-bottom: 0;
  }
}
</style>
