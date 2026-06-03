package com.sheepfarm.measurement.service;

import com.sheepfarm.measurement.config.WiFiDeviceProperties;
import com.sheepfarm.measurement.dto.BodyMeasurementCreateRequest;
import com.sheepfarm.measurement.dto.WiFiPullResult;
import com.sheepfarm.measurement.entity.BodyMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class WiFiPullService {

    private static final Logger log = LoggerFactory.getLogger(WiFiPullService.class);

    private final WiFiDeviceProperties wifiDeviceProperties;
    private final RestTemplate restTemplate;
    private final BodyMeasurementService bodyMeasurementService;

    public WiFiPullService(WiFiDeviceProperties wifiDeviceProperties,
                           RestTemplate restTemplate,
                           BodyMeasurementService bodyMeasurementService) {
        this.wifiDeviceProperties = wifiDeviceProperties;
        this.restTemplate = restTemplate;
        this.bodyMeasurementService = bodyMeasurementService;
    }

    public List<WiFiPullResult> pullFromAllDevices() {
        List<WiFiPullResult> results = new ArrayList<>();

        if (wifiDeviceProperties.getDevices().isEmpty()) {
            return results;
        }

        for (WiFiDeviceProperties.DeviceConfig device : wifiDeviceProperties.getDevices()) {
            WiFiPullResult result = pullFromDevice(device);
            results.add(result);
        }

        return results;
    }

    /**
     * 按设备名称拉取单台设备的数据
     */
    public WiFiPullResult pullByDeviceName(String deviceName) {
        WiFiDeviceProperties.DeviceConfig device = wifiDeviceProperties.getDevices()
                .stream()
                .filter(d -> d.getName().equals(deviceName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("设备不存在: " + deviceName));
        return pullFromDevice(device);
    }

    /**
     * 获取所有已配置的 WiFi 设备列表
     */
    public List<WiFiDeviceProperties.DeviceConfig> getDeviceList() {
        return wifiDeviceProperties.getDevices();
    }

    private WiFiPullResult pullFromDevice(WiFiDeviceProperties.DeviceConfig device) {
        try {
            log.info("start pulling data from device [{}]: {}", device.getName(), device.getUrl());
            BodyMeasurementCreateRequest data = restTemplate.getForObject(
                    device.getUrl(), BodyMeasurementCreateRequest.class);
            if (data == null) {
                log.warn("device [{}] returned null data", device.getName());
                return new WiFiPullResult(device.getName(), device.getUrl(), false, "设备返回了空数据", null);
            }
            BodyMeasurement saved = bodyMeasurementService.create(data);
            log.info("device [{}] data pulled and saved successfully, id={}", device.getName(), saved.getId());
            return new WiFiPullResult(device.getName(), device.getUrl(), true,
                    "拉取成功", saved.getId());
        } catch (Exception e) {
            log.error("failed to pull data from device [{}]: {}", device.getName(), e.getMessage());
            return new WiFiPullResult(device.getName(), device.getUrl(), false,
                    "拉取失败: " + e.getMessage(), null);
        }
    }
}
