package com.hzj.aliyun.provider.aliyun.ocr.enums;

/**
 * OCR 统一识别返回坐标格式。
 *
 * <p>对应 {@code OutputCoordinate} 参数，仅当设置该参数时才会返回文字坐标。</p>
 */
public enum AliyunOcrCoordinate {

    /** 四点坐标。 */
    POINTS("points"),

    /** 旋转矩形。 */
    RECTANGLE("rectangle");

    /** 阿里云 OutputCoordinate 参数取值。 */
    private final String code;

    /**
     * 创建坐标格式。
     *
     * @param code 阿里云 OutputCoordinate 参数取值
     */
    AliyunOcrCoordinate(String code) {
        this.code = code;
    }

    /**
     * 获取阿里云 OutputCoordinate 参数取值。
     *
     * @return OutputCoordinate 参数取值
     */
    public String getCode() {
        return code;
    }

    /**
     * 根据阿里云 OutputCoordinate 参数取值解析枚举。
     *
     * @param code OutputCoordinate 参数取值
     * @return 坐标格式；无法匹配时返回 {@code null}
     */
    public static AliyunOcrCoordinate fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AliyunOcrCoordinate coordinate : values()) {
            if (coordinate.code.equalsIgnoreCase(code)) {
                return coordinate;
            }
        }
        return null;
    }

    /**
     * 判断取值是否为受支持的坐标格式。
     *
     * @param code OutputCoordinate 参数取值
     * @return true-受支持，false-不受支持
     */
    public static boolean isSupported(String code) {
        return fromCode(code) != null;
    }
}
