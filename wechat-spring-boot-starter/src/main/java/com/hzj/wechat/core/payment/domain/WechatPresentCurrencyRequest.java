package com.hzj.wechat.core.payment.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.google.gson.annotations.SerializedName;

/**
 * 赠送用户代币请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatPresentCurrencyRequest extends WechatVirtualPaymentRequest {

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 赠送订单号。
     */
    @SerializedName("order_id")
    private String orderId;

    /**
     * 赠送代币数量。
     */
    private Long amount;
}
