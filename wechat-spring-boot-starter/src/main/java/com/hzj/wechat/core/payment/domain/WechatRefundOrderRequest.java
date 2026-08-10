package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 虚拟支付订单退款请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatRefundOrderRequest extends WechatVirtualPaymentRequest {

    /**
     * 下单用户 openid。
     */
    private String openid;

    /**
     * 商户订单号，与微信订单号二选一。
     */
    @SerializedName("order_id")
    private String orderId;

    /**
     * 微信内部订单号，与商户订单号二选一。
     */
    @SerializedName("wx_order_id")
    private String wxOrderId;

    /**
     * 本次退款单号。
     */
    @SerializedName("refund_order_id")
    private String refundOrderId;

    /**
     * 当前剩余可退款金额，单位为分。
     */
    @SerializedName("left_fee")
    private Long leftFee;

    /**
     * 本次退款金额，单位为分。
     */
    @SerializedName("refund_fee")
    private Long refundFee;

    /**
     * 商户自定义数据。
     */
    @SerializedName("biz_meta")
    private String bizMeta;

    /**
     * 退款原因。
     */
    @SerializedName("refund_reason")
    private String refundReason;

    /**
     * 退款来源。
     */
    @SerializedName("req_from")
    private String reqFrom;
}
