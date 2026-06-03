package com.sheepfarm.measurement.controller;

import com.sheepfarm.common.Result;
import com.sheepfarm.measurement.config.WiFiDeviceProperties;
import com.sheepfarm.measurement.dto.WiFiPullResult;
import com.sheepfarm.measurement.service.WiFiPullService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wifi")
public class WiFiPullController {

    private final WiFiPullService wifiPullService;

    public WiFiPullController(WiFiPullService wifiPullService) {
        this.wifiPullService = wifiPullService;
    }

    /**
     * 查看所有已配置的 WiFi 设备列表
     */
    @GetMapping("/devices")
    public Result<List<WiFiDeviceProperties.DeviceConfig>> listDevices() {
        return Result.success(wifiPullService.getDeviceList());
    }

    /**
     * 手动触发从所有配置的 WiFi 设备拉取测量数据
     */
    @PostMapping("/pull")
    public Result<List<WiFiPullResult>> pullAll() {
        List<WiFiPullResult> results = wifiPullService.pullFromAllDevices();
        return Result.success(results);
    }

    /**
     * 手动触发从指定设备名称的 WiFi 设备拉取测量数据
     */
    @PostMapping("/pull/{deviceName}")
    public Result<WiFiPullResult> pullByDevice(@PathVariable String deviceName) {
        WiFiPullResult result = wifiPullService.pullByDeviceName(deviceName);
        return Result.success(result);
    }
}
