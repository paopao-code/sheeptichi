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

    /**
     * 查询所有羊体测量数据。
     *
     * 调用方式：
     * GET http://localhost:8083/api/measurements
     *
     * 说明：
     * 返回数据按 measuredAt 测量时间倒序排列。
     */
    @GetMapping
    public Result<List<BodyMeasurement>> listAll() {
        return Result.success(bodyMeasurementService.listAll());
    }

    /**
     * 上传一条羊体测量数据。
     *
     * 调用方式：
     * POST http://localhost:8083/api/measurements
     * Content-Type: application/json
     *
     * 请求体示例：
     * {
     *   "penNo": "A01",
     *   "bodyLength": 120.5,
     *   "bodyHeight": 75.2,
     *   "chestWidth": 32.1,
     *   "chestDepth": 40.0,
     *   "chestGirth": 95.6
     * }
     *
     * 说明：
     * 前端或设备上传时不要传 id，id 由数据库自动自增。
     * 前端或设备上传时不要传 measuredAt，measuredAt 由后端自动填充当前时间。
     */
    @PostMapping
    public Result<BodyMeasurement> create(@Valid @RequestBody BodyMeasurementCreateRequest request) {
        return Result.success(bodyMeasurementService.create(request));
    }

    /**
     * 删除一条羊体测量数据。
     *
     * 调用方式：
     * DELETE http://localhost:8083/api/measurements/{id}
     *
     * 示例：
     * DELETE http://localhost:8083/api/measurements/1
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        bodyMeasurementService.deleteById(id);
        return Result.success();
    }
}
