package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 取消代币支付请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatCancelCurrencyPayRequest extends WechatVirtualPaymentRequest {
    public WechatCancelCurrencyPayRequest() { super(WechatVirtualPaymentApi.CANCEL_CURRENCY_PAY); }

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 用户 IP 地址。
     */
    @SerializedName("user_ip")
    private String userIp;

    /**
     * 原代币支付订单号。
     */
    @SerializedName("pay_order_id")
    private String payOrderId;

    /**
     * 本次退款订单号。
     */
    @SerializedName("order_id")
    private String orderId;

    /**
     * 退款代币数量。
     */
    private Long amount;
}
