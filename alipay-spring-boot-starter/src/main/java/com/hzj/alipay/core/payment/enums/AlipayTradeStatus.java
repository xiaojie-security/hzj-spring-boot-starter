package com.hzj.alipay.core.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付宝交易状态枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayTradeStatus {

    /**
     * 交易创建，等待买家付款。
     */
    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待买家付款"),

    /**
     * 未付款交易超时关闭，或支付完成后全额退款。
     */
    TRADE_CLOSED("TRADE_CLOSED", "未付款交易超时关闭，或支付完成后全额退款"),

    /**
     * 交易支付成功。
     */
    TRADE_SUCCESS("TRADE_SUCCESS", "交易支付成功"),

    /**
     * 交易结束，不可退款。
     */
    TRADE_FINISHED("TRADE_FINISHED", "交易结束，不可退款");

    /**
     * 状态码。
     */
    private final String status;

    /**
     * 状态描述。
     */
    private final String message;

    public static boolean isSuccess(String status) {
        return TRADE_SUCCESS.getStatus().equals(status);
    }

    public static boolean isClosed(String status) {
        return TRADE_CLOSED.getStatus().equals(status);
    }

    public static boolean isFinished(String status) {
        return TRADE_FINISHED.getStatus().equals(status);
    }

    public static boolean isWaitBuyerPay(String status) {
        return WAIT_BUYER_PAY.getStatus().equals(status);
    }

    public static AlipayTradeStatus fromStatus(String status) {
        for (AlipayTradeStatus value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
