package com.sheepfarm.measurement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BodyMeasurementCreateRequest {

    /**
     * 栏位号，对应 JSON 字段 penNo，例如 "A01"。
     */
    @NotBlank(message = "栏位号不能为空")
    private String penNo;

    /**
     * 体长，对应 JSON 字段 bodyLength，必须大于 0。
     */
    @NotNull(message = "体长不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "体长必须大于0")
    private BigDecimal bodyLength;

    /**
     * 体高，对应 JSON 字段 bodyHeight，必须大于 0。
     */
    @NotNull(message = "体高不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "体高必须大于0")
    private BigDecimal bodyHeight;

    /**
     * 胸宽，对应 JSON 字段 chestWidth，必须大于 0。
     */
    @NotNull(message = "胸宽不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "胸宽必须大于0")
    private BigDecimal chestWidth;

    /**
     * 胸深，对应 JSON 字段 chestDepth，必须大于 0。
     */
    @NotNull(message = "胸深不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "胸深必须大于0")
    private BigDecimal chestDepth;

    /**
     * 胸围，对应 JSON 字段 chestGirth，必须大于 0。
     *
     * 注意：
     * 上传数据时只传本类中的字段，不要传 id 和 measuredAt。
     */
    @NotNull(message = "胸围不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "胸围必须大于0")
    private BigDecimal chestGirth;
}
