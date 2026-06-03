export interface BodyMeasurement {
  id: number;
  penNo: string;
  bodyLength: number;
  bodyHeight: number;
  chestWidth: number;
  chestDepth: number;
  chestGirth: number;
  measuredAt: string;
}

export interface BodyMeasurementCreateRequest {
  penNo: string;
  bodyLength: number;
  bodyHeight: number;
  chestWidth: number;
  chestDepth: number;
  chestGirth: number;
}

export interface ApiResult<T> {
  code: number;
  msg: string;
  data: T;
}
