package com.hzj.amap.core.webapi.enums;

/**
 * 高德天气查询类型。
 */
public enum AMapWeatherType {

    /** 实况天气。 */
    LIVE("base"),

    /** 天气预报。 */
    FORECAST("all");

    /** 接口参数值。 */
    private final String value;

    AMapWeatherType(String value) {
        this.value = value;
    }

    /**
     * 获取接口参数值。
     *
     * @return 接口参数值
     */
    public String getValue() {
        return value;
    }
}
