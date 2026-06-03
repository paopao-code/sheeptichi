<template>
  <div class="admin-layout">
    <HeaderBar />
    <div class="admin-body">
      <SideBar />
      <main class="admin-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import HeaderBar from './components/HeaderBar.vue';
import SideBar from './components/SideBar.vue';
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background:
    linear-gradient(180deg, #eef4fb 0, #f5f7fb 220px),
    var(--color-bg);
}

.admin-body {
  display: flex;
  min-height: calc(100vh - 64px);
}

.admin-main {
  flex: 1;
  min-width: 0;
  padding: 24px;
  overflow-x: hidden;
}

@media (max-width: 768px) {
  .admin-body {
    flex-direction: column;
  }

  .admin-main {
    padding: 16px;
  }
}
</style>
