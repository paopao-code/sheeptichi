<template>
  <header class="header-bar">
    <div class="brand">
      <span class="brand-mark">
        <el-icon><DataAnalysis /></el-icon>
      </span>
      <span class="system-name">智慧羊场数据分析平台</span>
    </div>

    <div class="header-actions">
      <span class="health-pill" :class="{ online: healthOk }">
        <span class="health-dot"></span>
        {{ healthOk ? '后端在线' : '后端离线' }}
      </span>
    </div>
  </header>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { DataAnalysis } from '@element-plus/icons-vue';
import { fetchHealth } from '@/api/health';

const healthOk = ref(false);

onMounted(() => {
  loadHealth();
});

async function loadHealth() {
  try {
    const response = await fetchHealth();
    healthOk.value = response.data.data?.status === 'ok';
  } catch {
    healthOk.value = false;
  }
}
</script>

<style scoped>
.header-bar {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.94);
  border-bottom: 1px solid var(--color-border);
  backdrop-filter: blur(14px);
}

.brand,
.header-actions,
.health-pill {
  display: flex;
  align-items: center;
}

.brand {
  gap: 12px;
  min-width: 0;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  color: #fff;
  background: #2563eb;
  border-radius: var(--radius);
  font-size: 20px;
}

.system-name {
  color: #111827;
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
}

.header-actions {
  gap: 12px;
}

.health-pill {
  gap: 7px;
  min-width: 88px;
  height: 32px;
  padding: 0 11px;
  color: var(--color-muted);
  background: #f3f4f6;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  font-size: 13px;
}

.health-pill.online {
  color: var(--color-success);
  background: var(--color-success-soft);
  border-color: #bfe9db;
}

.health-dot {
  width: 7px;
  height: 7px;
  background: #9ca3af;
  border-radius: 50%;
}

.health-pill.online .health-dot {
  background: var(--color-success);
}

@media (max-width: 820px) {
  .header-bar {
    height: auto;
    min-height: 64px;
    align-items: flex-start;
    flex-direction: column;
    gap: 12px;
    padding: 14px 16px;
  }

  .header-actions {
    flex-wrap: wrap;
  }
}
</style>
