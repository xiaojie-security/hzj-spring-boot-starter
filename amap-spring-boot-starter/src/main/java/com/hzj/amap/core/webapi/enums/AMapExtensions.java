package com.hzj.amap.core.webapi.enums;

/**
 * 高德接口扩展信息返回级别。
 */
public enum AMapExtensions {

    /** 基础信息。 */
    BASE("base"),

    /** 完整扩展信息。 */
    ALL("all");

    /** 接口参数值。 */
    private final String value;

    AMapExtensions(String value) {
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
