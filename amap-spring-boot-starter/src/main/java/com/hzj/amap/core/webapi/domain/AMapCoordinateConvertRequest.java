package com.hzj.amap.core.webapi.domain;

import com.hzj.amap.core.webapi.enums.AMapCoordinateSystem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 坐标转换请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapCoordinateConvertRequest extends AMapWebApiRequest {

    /**
     * 待转换坐标，多个坐标以分号分隔。
     */
    private String locations;

    /**
     * 原始坐标系类型。
     */
    private AMapCoordinateSystem coordinateSystem;

    /**
     * 是否批量转换。
     */
    private Boolean batch;

    /**
     * 创建坐标转换请求。
     */
    public AMapCoordinateConvertRequest() {
        super("https://restapi.amap.com/v3/assistant/coordinate/convert");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "locations", locations);
        putIfNotBlank(parameters, "coordsys", coordinateSystem == null ? null : coordinateSystem.getValue());
        putIfNotBlank(parameters, "batch", batch);
        return parameters;
    }
}
