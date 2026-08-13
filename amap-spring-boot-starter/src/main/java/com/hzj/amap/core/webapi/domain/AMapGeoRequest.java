package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 地理编码请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapGeoRequest extends AMapWebApiRequest {

    /**
     * 待解析结构化地址。
     */
    private String address;

    /**
     * 指定查询城市。
     */
    private String city;

    /**
     * 是否返回扩展地址信息。
     */
    private Boolean batch;

    /**
     * 创建地理编码请求。
     */
    public AMapGeoRequest() {
        super("https://restapi.amap.com/v3/geocode/geo");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "address", address);
        putIfNotBlank(parameters, "city", city);
        putIfNotBlank(parameters, "batch", batch);
        return parameters;
    }
}
