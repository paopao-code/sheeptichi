import request from './request';
import type { ApiResult } from '@/types/measurement';

export interface HealthStatus {
  status: string;
  service: string;
}

export function fetchHealth() {
  return request.get<ApiResult<HealthStatus>>('/health');
}
