package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 关闭订单请求参数。
 */
public class PaymentCloseOrderRequest {
    /**
     * 商户订单号。
     */
    @Expose(serialize = false)
    public String outTradeNo;

    /**
     * 商户号。
     * 关闭订单接口要求放在请求体中。
     * 如果未传，则服务层会自动回退使用已初始化的商户号。
     */
    @SerializedName("mchid")
    public String mchid;
}
