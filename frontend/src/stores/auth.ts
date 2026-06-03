import { defineStore } from 'pinia';
import { clearAuth, getToken, getUser, setToken, setUser, type LoginUser } from '@/utils/auth';

interface AuthState {
  token: string;
  user: LoginUser | null;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: getToken(),
    user: getUser()
  }),
  getters: {
    isAuthenticated: state => Boolean(state.token),
    username: state => state.user?.username || '未登录'
  },
  actions: {
    mockLogin(username: string, password: string) {
      if (!username.trim() || !password.trim()) {
        throw new Error('请输入用户名和密码');
      }

      const user = { username: 'admin', role: 'admin' };
      this.token = 'mock-token';
      this.user = user;
      setToken(this.token);
      setUser(user);
    },
    logout() {
      this.token = '';
      this.user = null;
      clearAuth();
    }
  }
});
