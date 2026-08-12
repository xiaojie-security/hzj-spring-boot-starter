package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 虚拟支付发货通知请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatNotifyProvideGoodsRequest extends WechatVirtualPaymentRequest {
    public WechatNotifyProvideGoodsRequest() { super(WechatVirtualPaymentApi.NOTIFY_PROVIDE_GOODS); }

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
