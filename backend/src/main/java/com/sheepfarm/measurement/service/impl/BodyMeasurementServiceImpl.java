package com.sheepfarm.measurement.service.impl;

import com.sheepfarm.measurement.dto.BodyMeasurementCreateRequest;
import com.sheepfarm.measurement.entity.BodyMeasurement;
import com.sheepfarm.measurement.mapper.BodyMeasurementMapper;
import com.sheepfarm.measurement.service.BodyMeasurementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BodyMeasurementServiceImpl implements BodyMeasurementService {

    private final BodyMeasurementMapper bodyMeasurementMapper;

    public BodyMeasurementServiceImpl(BodyMeasurementMapper bodyMeasurementMapper) {
        this.bodyMeasurementMapper = bodyMeasurementMapper;
    }

    @Override
    public List<BodyMeasurement> listAll() {
        return bodyMeasurementMapper.findAll();
    }

    @Override
    @Transactional
    public BodyMeasurement create(BodyMeasurementCreateRequest request) {
        BodyMeasurement measurement = new BodyMeasurement();
        measurement.setPenNo(request.getPenNo());
        measurement.setBodyLength(request.getBodyLength());
        measurement.setBodyHeight(request.getBodyHeight());
        measurement.setChestWidth(request.getChestWidth());
        measurement.setChestDepth(request.getChestDepth());
        measurement.setChestGirth(request.getChestGirth());
        measurement.setMeasuredAt(LocalDateTime.now());

        bodyMeasurementMapper.insert(measurement);
        return measurement;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        int affectedRows = bodyMeasurementMapper.deleteById(id);
        if (affectedRows == 0) {
            throw new IllegalArgumentException("测量数据不存在");
        }
    }
}
