package com.hzj.amap.core.webapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * IP 定位请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapIpLocationRequest extends AMapWebApiRequest {

    /**
     * 待定位 IPv4 地址，不传时定位请求来源 IP。
     */
    private String ip;

    /**
     * 创建 IP 定位请求。
     */
    public AMapIpLocationRequest() {
        super("https://restapi.amap.com/v3/ip");
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "ip", ip);
        return parameters;
    }
}
