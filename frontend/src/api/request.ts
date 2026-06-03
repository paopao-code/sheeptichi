import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';
import { clearAuth, getToken } from '@/utils/auth';

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
});

request.interceptors.request.use(config => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
    config.headers.token = token;
  }
  return config;
});

request.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      clearAuth();
      ElMessage.error('登录状态已失效，请重新登录');
      router.push('/login');
    }
    return Promise.reject(error);
  }
);

export default request;
