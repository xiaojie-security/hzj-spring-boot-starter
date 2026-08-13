package com.hzj.amap.core.webapi.enums;

/**
 * 高德路径规划 2.0 出行方式。
 */
public enum AMapRouteMode {

    /**
     * 驾车路径规划。
     */
    DRIVING("https://restapi.amap.com/v5/direction/driving"),

    /**
     * 步行路径规划。
     */
    WALKING("https://restapi.amap.com/v5/direction/walking"),

    /**
     * 骑行路径规划。
     */
    BICYCLING("https://restapi.amap.com/v5/direction/bicycling"),

    /**
     * 公交路径规划。
     */
    TRANSIT("https://restapi.amap.com/v5/direction/transit/integrated"),

    /**
     * 货车路径规划。
     */
    TRUCK("https://restapi.amap.com/v5/direction/truck"),

    /**
     * 电动车路径规划。
     */
    ELECTRIC_BICYCLE("https://restapi.amap.com/v5/direction/electrobike");

    /**
     * 默认请求地址。
     */
    private final String requestUrl;

    AMapRouteMode(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * 获取默认请求地址。
     *
     * @return 请求地址
     */
    public String getRequestUrl() {
        return requestUrl;
    }
}
