package com.hzj.amap.core.webapi.enums;

/**
 * 高德行政区域下级查询深度。
 */
public enum AMapDistrictDepth {

    /** 不返回下级行政区。 */
    NONE("0"),

    /** 返回一级下级行政区。 */
    ONE("1"),

    /** 返回二级下级行政区。 */
    TWO("2"),

    /** 返回三级下级行政区。 */
    THREE("3");

    /** 接口参数值。 */
    private final String value;

    AMapDistrictDepth(String value) {
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
