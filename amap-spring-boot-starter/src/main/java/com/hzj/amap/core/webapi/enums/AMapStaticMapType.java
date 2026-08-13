package com.hzj.amap.core.webapi.enums;

/**
 * 高德静态地图底图类型。
 */
public enum AMapStaticMapType {

    /** 普通道路地图。 */
    ROADMAP("roadmap"),

    /** 卫星地图。 */
    SATELLITE("satellite");

    /** 接口参数值。 */
    private final String value;

    AMapStaticMapType(String value) {
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
