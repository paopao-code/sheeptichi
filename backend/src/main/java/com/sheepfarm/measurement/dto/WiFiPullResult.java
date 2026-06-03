package com.sheepfarm.measurement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WiFiPullResult {

    /** 设备名称 */
    private String deviceName;

    /** 设备 URL */
    private String deviceUrl;

    /** 是否拉取成功 */
    private boolean success;

    /** 结果描述信息 */
    private String message;

    /** 成功入库时的测量数据 ID */
    private Long measurementId;
}
