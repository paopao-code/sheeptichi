package com.sheepfarm.measurement.controller;

import com.sheepfarm.common.Result;
import com.sheepfarm.measurement.dto.BodyMeasurementCreateRequest;
import com.sheepfarm.measurement.entity.BodyMeasurement;
import com.sheepfarm.measurement.service.BodyMeasurementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wifi/push")
public class WiFiPushController {

    private final BodyMeasurementService bodyMeasurementService;

    public WiFiPushController(BodyMeasurementService bodyMeasurementService) {
        this.bodyMeasurementService = bodyMeasurementService;
    }

    @PostMapping
    public Result<BodyMeasurement> receivePush(@Valid @RequestBody BodyMeasurementCreateRequest request) {
        return Result.success(bodyMeasurementService.create(request));
    }
}
