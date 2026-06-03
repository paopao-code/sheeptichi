<template>
  <div class="login-page">
    <section class="login-shell">
      <div class="login-brand">
        <span class="brand-mark">
          <el-icon><DataAnalysis /></el-icon>
        </span>
        <h1>智慧羊场数据分析平台</h1>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submitLogin">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model.trim="form.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model.trim="form.password"
            placeholder="请输入密码"
            type="password"
            show-password
            size="large"
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-button class="login-button" type="primary" size="large" :loading="loading" @click="submitLogin">
          登录
        </el-button>
      </el-form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { DataAnalysis, Lock, User } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive({
  username: '',
  password: ''
});

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

async function submitLogin() {
  if (!formRef.value) {
    return;
  }

  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) {
    return;
  }

  loading.value = true;
  try {
    authStore.mockLogin(form.username, form.password);
    ElMessage.success('登录成功');
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/data-display';
    router.push(redirect);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background:
    linear-gradient(135deg, rgba(37, 99, 235, 0.12), rgba(15, 139, 111, 0.09)),
    #f5f7fb;
}

.login-shell {
  width: min(420px, 100%);
  padding: 34px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(229, 231, 235, 0.92);
  border-radius: var(--radius);
  box-shadow: 0 24px 54px rgba(15, 23, 42, 0.12);
}

.login-brand {
  margin-bottom: 28px;
  text-align: center;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  margin-bottom: 14px;
  color: #fff;
  background: var(--color-primary);
  border-radius: var(--radius);
  font-size: 24px;
}

.login-brand h1 {
  margin: 0;
  color: #111827;
  font-size: 22px;
  font-weight: 800;
}

.login-button {
  width: 100%;
  height: 44px;
  margin-top: 10px;
}
</style>
