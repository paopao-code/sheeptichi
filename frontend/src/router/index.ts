import { createRouter, createWebHistory } from 'vue-router';
import AdminLayout from '@/layout/AdminLayout.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      redirect: '/data-display'
    },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/data-display',
      children: [
        {
          path: 'data-display',
          name: 'data-display',
          component: () => import('@/views/data-display/DataDisplayView.vue'),
          meta: { title: '数据展示' }
        },
        {
          path: 'visual-analysis',
          name: 'visual-analysis',
          component: () => import('@/views/visual-analysis/VisualAnalysisView.vue'),
          meta: { title: '数据可视化分析' }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/data-display'
    }
  ]
});

export default router;
