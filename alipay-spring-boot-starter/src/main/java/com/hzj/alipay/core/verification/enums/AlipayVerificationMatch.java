package com.hzj.alipay.core.verification.enums;

import lombok.Getter;

/**
 * 支付宝核验匹配结果。
 */
@Getter
public enum AlipayVerificationMatch {

    /** 核验一致。 */
    MATCH("一致"),
    /** 核验不一致。 */
    NOT_MATCH("不一致"),
    /** SDK 返回了未识别的结果。 */
    UNKNOWN(null);

    private final String code;

    AlipayVerificationMatch(String code) {
        this.code = code;
    }

    /**
     * 将支付宝返回值转换为枚举。
     *
     * @param code 支付宝返回值
     * @return 匹配结果
     */
    public static AlipayVerificationMatch fromCode(String code) {
        if ("一致".equals(code) || "Y".equalsIgnoreCase(code) || "true".equalsIgnoreCase(code)) {
            return MATCH;
        }
        if ("不一致".equals(code) || "N".equalsIgnoreCase(code) || "false".equalsIgnoreCase(code)) {
            return NOT_MATCH;
        }
        return UNKNOWN;
    }
}
