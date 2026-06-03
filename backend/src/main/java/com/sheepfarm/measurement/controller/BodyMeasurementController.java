package com.sheepfarm.measurement.controller;

import com.sheepfarm.common.Result;
import com.sheepfarm.measurement.dto.BodyMeasurementCreateRequest;
import com.sheepfarm.measurement.entity.BodyMeasurement;
import com.sheepfarm.measurement.service.BodyMeasurementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class BodyMeasurementController {

    private final BodyMeasurementService bodyMeasurementService;

    public BodyMeasurementController(BodyMeasurementService bodyMeasurementService) {
        this.bodyMeasurementService = bodyMeasurementService;
    }

    @GetMapping
    public Result<List<BodyMeasurement>> listAll() {
        return Result.success(bodyMeasurementService.listAll());
    }

    @PostMapping
    public Result<BodyMeasurement> create(@Valid @RequestBody BodyMeasurementCreateRequest request) {
        return Result.success(bodyMeasurementService.create(request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        bodyMeasurementService.deleteById(id);
        return Result.success();
    }
}
