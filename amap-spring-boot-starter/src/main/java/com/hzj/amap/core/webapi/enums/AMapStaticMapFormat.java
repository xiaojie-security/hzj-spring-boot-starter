package com.hzj.amap.core.webapi.enums;

/**
 * 高德静态地图图片格式。
 */
public enum AMapStaticMapFormat {

    /** PNG 图片。 */
    PNG("png"),

    /** PNG8 图片。 */
    PNG8("png8"),

    /** JPG 图片。 */
    JPG("jpg");

    /** 接口参数值。 */
    private final String value;

    AMapStaticMapFormat(String value) {
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
