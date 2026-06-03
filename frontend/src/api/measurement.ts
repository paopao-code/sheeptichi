import request from './request';
import type { ApiResult, BodyMeasurement, BodyMeasurementCreateRequest } from '@/types/measurement';

export function fetchMeasurements() {
  return request.get<ApiResult<BodyMeasurement[]>>('/measurements');
}

export function createMeasurement(data: BodyMeasurementCreateRequest) {
  return request.post<ApiResult<BodyMeasurement>>('/measurements', data);
}

export function deleteMeasurement(id: number) {
  return request.delete<ApiResult<null>>(`/measurements/${id}`);
}
