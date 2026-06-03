package com.sheepfarm.measurement.service;

import com.sheepfarm.measurement.dto.BodyMeasurementCreateRequest;
import com.sheepfarm.measurement.entity.BodyMeasurement;

import java.util.List;

public interface BodyMeasurementService {

    List<BodyMeasurement> listAll();

    BodyMeasurement create(BodyMeasurementCreateRequest request);

    void deleteById(Long id);
}
