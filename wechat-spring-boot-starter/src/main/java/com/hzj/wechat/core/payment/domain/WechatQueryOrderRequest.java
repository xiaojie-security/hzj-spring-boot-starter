package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询虚拟支付订单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryOrderRequest extends WechatVirtualPaymentRequest {

    /**
     * 用户 openid。
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
}
