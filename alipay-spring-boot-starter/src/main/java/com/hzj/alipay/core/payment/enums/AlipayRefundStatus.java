package com.hzj.alipay.core.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付宝退款状态枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayRefundStatus {

    /**
     * 退款成功。
     */
    REFUND_SUCCESS("REFUND_SUCCESS", "退款处理成功");

    /**
     * 状态码。
     */
    private final String status;

    /**
     * 状态描述。
     */
    private final String message;

    public static boolean isSuccess(String status) {
        return REFUND_SUCCESS.getStatus().equals(status);
    }

    public static AlipayRefundStatus fromStatus(String status) {
        for (AlipayRefundStatus value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
