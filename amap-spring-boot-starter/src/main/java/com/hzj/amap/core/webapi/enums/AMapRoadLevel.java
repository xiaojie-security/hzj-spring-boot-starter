package com.hzj.amap.core.webapi.enums;

/**
 * 高德逆地理编码道路等级。
 */
public enum AMapRoadLevel {

    /** 返回所有道路。 */
    ALL("0"),

    /** 仅返回主干道路。 */
    MAIN_ROAD("1");

    /** 接口参数值。 */
    private final String value;

    AMapRoadLevel(String value) {
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
