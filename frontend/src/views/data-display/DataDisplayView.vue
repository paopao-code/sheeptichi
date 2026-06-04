<template>
  <section>
    <div class="page-title-row">
      <div>
        <h1>羊体测量数据展示</h1>
        <div class="muted-text">后端状态：{{ healthText }}</div>
      </div>
      <el-button type="primary" size="large" :icon="Plus" @click="openCreateDialog">
        新增测量数据
      </el-button>
    </div>

    <div class="metric-grid">
      <div class="metric-card">
        <span class="metric-icon primary">
          <el-icon><Document /></el-icon>
        </span>
        <div>
          <div class="metric-value">{{ measurements.length }}</div>
          <div class="metric-label">记录总数</div>
        </div>
      </div>
      <div class="metric-card">
        <span class="metric-icon success">
          <el-icon><OfficeBuilding /></el-icon>
        </span>
        <div>
          <div class="metric-value">{{ penCount }}</div>
          <div class="metric-label">栏位数量</div>
        </div>
      </div>
      <div class="metric-card">
        <span class="metric-icon" :class="healthOk ? 'success' : 'danger'">
          <el-icon><Connection /></el-icon>
        </span>
        <div>
          <div class="metric-value">{{ healthText }}</div>
          <div class="metric-label">接口联通</div>
        </div>
      </div>
    </div>

    <div class="page-panel table-panel">
      <el-table
        v-loading="loading"
        :data="measurements"
        stripe
        empty-text="暂无测量数据"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="penNo" label="栏位号" min-width="110" align="center">
          <template #default="{ row }">
            <el-tag effect="plain">{{ row.penNo }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bodyLength" label="体长" min-width="100" align="center" />
        <el-table-column prop="bodyHeight" label="体高" min-width="100" align="center" />
        <el-table-column prop="chestWidth" label="胸宽" min-width="100" align="center" />
        <el-table-column prop="chestDepth" label="胸深" min-width="100" align="center" />
        <el-table-column prop="chestGirth" label="胸围" min-width="100" align="center" />
        <el-table-column label="测量时间" min-width="180" align="center">
          <template #default="{ row }">
            <span class="time-cell">{{ formatDateTime(row.measuredAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="删除" placement="top">
              <el-button type="danger" link :icon="Delete" @click="handleDelete(row.id)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="新增测量数据" width="520px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="86px">
        <el-form-item label="栏位号" prop="penNo">
          <el-input v-model.trim="form.penNo" placeholder="例如 A01" />
        </el-form-item>
        <el-form-item label="体长" prop="bodyLength">
          <el-input-number v-model="form.bodyLength" :min="0" :precision="2" :step="0.1" controls-position="right" />
        </el-form-item>
        <el-form-item label="体高" prop="bodyHeight">
          <el-input-number v-model="form.bodyHeight" :min="0" :precision="2" :step="0.1" controls-position="right" />
        </el-form-item>
        <el-form-item label="胸宽" prop="chestWidth">
          <el-input-number v-model="form.chestWidth" :min="0" :precision="2" :step="0.1" controls-position="right" />
        </el-form-item>
        <el-form-item label="胸深" prop="chestDepth">
          <el-input-number v-model="form.chestDepth" :min="0" :precision="2" :step="0.1" controls-position="right" />
        </el-form-item>
        <el-form-item label="胸围" prop="chestGirth">
          <el-input-number v-model="form.chestGirth" :min="0" :precision="2" :step="0.1" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Connection, Delete, Document, OfficeBuilding, Plus } from '@element-plus/icons-vue';
import { fetchHealth } from '@/api/health';
import { createMeasurement, deleteMeasurement, fetchMeasurements } from '@/api/measurement';
import type { BodyMeasurement, BodyMeasurementCreateRequest } from '@/types/measurement';

const measurements = ref<BodyMeasurement[]>([]);
const loading = ref(false);
const saving = ref(false);
const dialogVisible = ref(false);
const healthText = ref('检测中');
const healthOk = ref(false);
const formRef = ref<FormInstance>();

const penCount = computed(() => new Set(measurements.value.map(item => item.penNo)).size);

const createDefaultForm = (): BodyMeasurementCreateRequest => ({
  penNo: '',
  bodyLength: 0,
  bodyHeight: 0,
  chestWidth: 0,
  chestDepth: 0,
  chestGirth: 0
});

const form = reactive<BodyMeasurementCreateRequest>(createDefaultForm());

const positiveNumberRule = [
  { required: true, type: 'number' as const, min: 0.01, message: '请输入大于 0 的数值', trigger: 'change' }
];

const rules: FormRules = {
  penNo: [{ required: true, message: '请输入栏位号', trigger: 'blur' }],
  bodyLength: positiveNumberRule,
  bodyHeight: positiveNumberRule,
  chestWidth: positiveNumberRule,
  chestDepth: positiveNumberRule,
  chestGirth: positiveNumberRule
};

onMounted(() => {
  loadHealth();
  loadMeasurements();
});

async function loadHealth() {
  try {
    const response = await fetchHealth();
    const ok = response.data.data.status === 'ok';
    healthText.value = ok ? '已连接' : '异常';
    healthOk.value = ok;
  } catch {
    healthText.value = '未连接';
    healthOk.value = false;
  }
}

async function loadMeasurements() {
  loading.value = true;
  try {
    const response = await fetchMeasurements();
    measurements.value = response.data.data || [];
  } catch {
    ElMessage.error('数据加载失败');
  } finally {
    loading.value = false;
  }
}

function openCreateDialog() {
  dialogVisible.value = true;
}

async function submitCreate() {
  if (!formRef.value) {
    return;
  }

  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) {
    return;
  }

  saving.value = true;
  try {
    await createMeasurement({ ...form });
    ElMessage.success('新增成功');
    dialogVisible.value = false;
    await loadMeasurements();
  } catch {
    ElMessage.error('新增失败');
  } finally {
    saving.value = false;
  }
}

async function handleDelete(id: number) {
  const confirmed = await ElMessageBox.confirm('确定删除这条记录吗？', '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  }).catch(() => false);

  if (!confirmed) {
    return;
  }

  try {
    await deleteMeasurement(id);
    ElMessage.success('已删除');
    await loadMeasurements();
  } catch {
    ElMessage.error('删除失败');
  }
}

function resetForm() {
  Object.assign(form, createDefaultForm());
  formRef.value?.clearValidate();
}

function formatDateTime(value: string) {
  return value ? value.replace('T', ' ') : '--';
}
</script>

<style scoped>
.table-panel {
  padding: 8px 0;
  overflow: hidden;
}

.time-cell {
  color: var(--color-muted);
  font-size: 13px;
}
</style>
