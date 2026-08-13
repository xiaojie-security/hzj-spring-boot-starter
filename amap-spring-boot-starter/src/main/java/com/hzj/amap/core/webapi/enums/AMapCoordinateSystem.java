package com.hzj.amap.core.webapi.enums;

/**
 * 高德坐标转换原始坐标系。
 */
public enum AMapCoordinateSystem {

    /** GPS 坐标。 */
    GPS("gps"),

    /** 图吧坐标。 */
    MAPBAR("mapbar"),

    /** 百度坐标。 */
    BAIDU("baidu"),

    /** 高德坐标。 */
    AUTONAVI("autonavi");

    /** 接口参数值。 */
    private final String value;

    AMapCoordinateSystem(String value) {
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
