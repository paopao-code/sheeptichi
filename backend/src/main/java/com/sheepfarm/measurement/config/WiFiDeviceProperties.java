package com.sheepfarm.measurement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "wifi")
public class WiFiDeviceProperties {

    /**
     * Pull 拉取间隔（毫秒），默认 60000（60 秒）
     */
    private long pullIntervalMs = 60000;

    /**
     * WiFi 测量设备列表
     */
    private List<DeviceConfig> devices = new ArrayList<>();

    @Data
    public static class DeviceConfig {

        /** 设备名称，用于日志标识 */
        private String name;

        /** 设备 HTTP 端点地址，如 http://192.168.1.100:8080/api/data */
        private String url;
    }
}
