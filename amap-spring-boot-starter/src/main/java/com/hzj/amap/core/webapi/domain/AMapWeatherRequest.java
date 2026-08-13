package com.hzj.amap.core.webapi.domain;

import com.hzj.amap.core.webapi.enums.AMapWeatherType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 天气查询请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapWeatherRequest extends AMapWebApiRequest {

    /**
     * 城市编码。
     */
    private String city;

    /**
     * 气象类型，base 表示实况天气，all 表示预报天气。
     */
    private AMapWeatherType weatherType = AMapWeatherType.LIVE;

    /**
     * 创建天气查询请求。
     */
    public AMapWeatherRequest() {
        super("https://restapi.amap.com/v3/weather/weatherInfo");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "city", city);
        putIfNotBlank(parameters, "extensions", weatherType == null ? null : weatherType.getValue());
        return parameters;
    }
}
