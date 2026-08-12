package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 关闭订单请求参数。
 */
public class PaymentCloseOrderRequest extends WechatPaymentApiRequest {
    /**
     * 创建关闭订单请求参数。
     */
    public PaymentCloseOrderRequest() {
        requestPath = "/v3/pay/transactions/out-trade-no/{out_trade_no}/close";
        requestMethod = WechatHttpMethod.POST;
    }
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
