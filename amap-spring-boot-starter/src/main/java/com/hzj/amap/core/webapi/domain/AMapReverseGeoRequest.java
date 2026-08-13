package com.hzj.amap.core.webapi.domain;

import com.hzj.amap.core.webapi.enums.AMapExtensions;
import com.hzj.amap.core.webapi.enums.AMapRoadLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 逆地理编码请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapReverseGeoRequest extends AMapWebApiRequest {

    /**
     * 经纬度坐标，格式为经度,纬度。
     */
    private String location;

    /**
     * 搜索半径，单位为米。
     */
    private Integer radius;

    /**
     * 返回结果扩展级别。
     */
    private AMapExtensions extensions = AMapExtensions.BASE;

    /**
     * 道路等级。
     */
    private AMapRoadLevel roadLevel;

    /**
     * 创建逆地理编码请求。
     */
    public AMapReverseGeoRequest() {
        super("https://restapi.amap.com/v3/geocode/regeo");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "location", location);
        putIfNotBlank(parameters, "radius", radius);
        putIfNotBlank(parameters, "extensions", extensions == null ? null : extensions.getValue());
        putIfNotBlank(parameters, "roadlevel", roadLevel == null ? null : roadLevel.getValue());
        return parameters;
    }
}
