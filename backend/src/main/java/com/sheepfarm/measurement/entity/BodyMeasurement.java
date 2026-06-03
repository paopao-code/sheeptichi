package com.sheepfarm.measurement.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BodyMeasurement {

    private Long id;
    private String penNo;
    private BigDecimal bodyLength;
    private BigDecimal bodyHeight;
    private BigDecimal chestWidth;
    private BigDecimal chestDepth;
    private BigDecimal chestGirth;
    private LocalDateTime measuredAt;
}
