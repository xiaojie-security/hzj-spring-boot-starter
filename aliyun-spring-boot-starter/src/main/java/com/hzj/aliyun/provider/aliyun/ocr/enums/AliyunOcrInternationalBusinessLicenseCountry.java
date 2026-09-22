package com.hzj.aliyun.provider.aliyun.ocr.enums;

/**
 * OCR 国际企业执照（Type=InternationalBusinessLicense）支持的国家。
 *
 * <p>对应 {@code InternationalBusinessLicenseConfig.Country} 参数；不传时由算法自动判断，
 * 指定国家可缩短接口响应时间。</p>
 */
public enum AliyunOcrInternationalBusinessLicenseCountry {

    /** 印度。 */
    INDIA("India"),

    /** 韩国。 */
    KOREA("Korea");

    /** 阿里云 Country 参数取值。 */
    private final String code;

    /**
     * 创建国家。
     *
     * @param code 阿里云 Country 参数取值
     */
    AliyunOcrInternationalBusinessLicenseCountry(String code) {
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
    public static AliyunOcrInternationalBusinessLicenseCountry fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AliyunOcrInternationalBusinessLicenseCountry country : values()) {
            if (country.code.equalsIgnoreCase(code)) {
                return country;
            }
        }
        return null;
    }
}
