package com.sheepfarm.measurement.mapper;

import com.sheepfarm.measurement.entity.BodyMeasurement;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BodyMeasurementMapper {

    @Select("""
            SELECT id,
                   pen_no,
                   body_length,
                   body_height,
                   chest_width,
                   chest_depth,
                   chest_girth,
                   measured_at
            FROM body_measurement
            ORDER BY measured_at DESC, id DESC
            """)
    List<BodyMeasurement> findAll();

    @Insert("""
            INSERT INTO body_measurement (
                pen_no,
                body_length,
                body_height,
                chest_width,
                chest_depth,
                chest_girth,
                measured_at
            ) VALUES (
                #{penNo},
                #{bodyLength},
                #{bodyHeight},
                #{chestWidth},
                #{chestDepth},
                #{chestGirth},
                #{measuredAt}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BodyMeasurement bodyMeasurement);

    @Delete("DELETE FROM body_measurement WHERE id = #{id}")
    int deleteById(Long id);
}
