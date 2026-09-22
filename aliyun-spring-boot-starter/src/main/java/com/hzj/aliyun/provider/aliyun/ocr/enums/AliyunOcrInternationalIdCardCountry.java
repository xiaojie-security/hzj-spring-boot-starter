package com.hzj.aliyun.provider.aliyun.ocr.enums;

/**
 * OCR 国际身份证（Type=InternationalIdCard）支持的国家。
 *
 * <p>对应 {@code InternationalIdCardConfig.Country} 参数；不传时由算法自动判断，
 * 指定国家可缩短接口响应时间。</p>
 */
public enum AliyunOcrInternationalIdCardCountry {

    /** 孟加拉国。 */
    BANGLADESH("Bangladesh"),

    /** 越南。 */
    VIETNAM("Vietnam"),

    /** 韩国。 */
    KOREA("Korea"),

    /** 印度。 */
    INDIA("India");

    /** 阿里云 Country 参数取值。 */
    private final String code;

    /**
     * 创建国家。
     *
     * @param code 阿里云 Country 参数取值
     */
    AliyunOcrInternationalIdCardCountry(String code) {
        this.code = code;
    }

    /**
     * 获取阿里云 Country 参数取值。
     *
     * @return Country 参数取值
     */
    public String getCode() {
        return code;
    }

    /**
     * 根据阿里云 Country 参数取值解析枚举。
     *
     * @param code Country 参数取值
     * @return 国家；无法匹配时返回 {@code null}
     */
    public static AliyunOcrInternationalIdCardCountry fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AliyunOcrInternationalIdCardCountry country : values()) {
            if (country.code.equalsIgnoreCase(code)) {
                return country;
            }
        }
        return null;
    }
}
