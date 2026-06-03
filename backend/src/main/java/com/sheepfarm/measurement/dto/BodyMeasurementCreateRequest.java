package com.sheepfarm.measurement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BodyMeasurementCreateRequest {

    @NotBlank(message = "栏位号不能为空")
    private String penNo;

    @NotNull(message = "体长不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "体长必须大于0")
    private BigDecimal bodyLength;

    @NotNull(message = "体高不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "体高必须大于0")
    private BigDecimal bodyHeight;

    @NotNull(message = "胸宽不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "胸宽必须大于0")
    private BigDecimal chestWidth;

    @NotNull(message = "胸深不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "胸深必须大于0")
    private BigDecimal chestDepth;

    @NotNull(message = "胸围不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "胸围必须大于0")
    private BigDecimal chestGirth;
}
