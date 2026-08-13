package com.hzj.amap.core.webapi.domain;

import com.hzj.amap.core.webapi.enums.AMapRouteMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 路径规划 2.0 请求参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AMapRouteRequest extends AMapWebApiRequest {

    /**
     * 出行方式。
     */
    private AMapRouteMode routeMode = AMapRouteMode.DRIVING;

    /**
     * 起点坐标，格式为经度,纬度。
     */
    private String origin;

    /**
     * 终点坐标，格式为经度,纬度。
     */
    private String destination;

    /**
     * 途经点坐标，多个以分号分隔。
     */
    private String waypoints;

    /**
     * 驾车策略。
     */
    private Integer strategy;

    /**
     * 公交城市。
     */
    private String city1;

    /**
     * 终点公交城市。
     */
    private String city2;

    /**
     * 创建路径规划请求。
     */
    public AMapRouteRequest() {
        super(null);
    }

    /**
     * 获取实际请求地址。
     *
     * @return 请求地址
     */
    public String resolveRequestUrl() {
        return getRequestUrl() == null || getRequestUrl().trim().isEmpty()
                ? routeMode.getRequestUrl() : getRequestUrl();
    }

    @Override
    public Map<String, String> toQueryParameters() {
        Map<String, String> parameters = createParameters();
        putIfNotBlank(parameters, "origin", origin);
        putIfNotBlank(parameters, "destination", destination);
        putIfNotBlank(parameters, "waypoints", waypoints);
        putIfNotBlank(parameters, "strategy", strategy);
        putIfNotBlank(parameters, "city1", city1);
        putIfNotBlank(parameters, "city2", city2);
        return parameters;
    }
}
