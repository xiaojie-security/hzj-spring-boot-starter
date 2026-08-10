package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转账状态枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayTransferStatus {

    SUCCESS("SUCCESS", "成功"),
    WAIT_PAY("WAIT_PAY", "等待支付"),
    CLOSED("CLOSED", "已关闭"),
    FAIL("FAIL", "失败"),
    DEALING("DEALING", "处理中"),
    REFUND("REFUND", "已退款");

    private final String code;

    private final String message;

    public static AlipayTransferStatus fromCode(String code) {
        for (AlipayTransferStatus value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
