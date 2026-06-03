package com.sheepfarm.measurement.scheduler;

import com.sheepfarm.measurement.dto.WiFiPullResult;
import com.sheepfarm.measurement.service.WiFiPullService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WiFiPullScheduler {

    private static final Logger log = LoggerFactory.getLogger(WiFiPullScheduler.class);

    private final WiFiPullService wifiPullService;

    public WiFiPullScheduler(WiFiPullService wifiPullService) {
        this.wifiPullService = wifiPullService;
    }

    @Scheduled(fixedDelayString = "${wifi.pull-interval-ms:60000}")
    public void pullFromAllDevices() {
        List<WiFiPullResult> results = wifiPullService.pullFromAllDevices();
        for (WiFiPullResult result : results) {
            if (result.isSuccess()) {
                log.info("scheduler: device [{}] pulled ok, measurementId={}",
                        result.getDeviceName(), result.getMeasurementId());
            } else {
                log.warn("scheduler: device [{}] pull failed: {}", result.getDeviceName(), result.getMessage());
            }
        }
    }
}
