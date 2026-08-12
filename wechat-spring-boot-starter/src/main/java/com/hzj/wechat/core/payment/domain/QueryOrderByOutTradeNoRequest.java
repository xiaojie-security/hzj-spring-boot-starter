package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 通过商户订单号查询订单请求参数。
 */
public class QueryOrderByOutTradeNoRequest extends WechatPaymentApiRequest {
    /**
     * 创建按商户订单号查询订单请求参数。
     */
    public QueryOrderByOutTradeNoRequest() {
        requestPath = "/v3/pay/transactions/out-trade-no/{out_trade_no}";
        requestMethod = WechatHttpMethod.GET;
    }
    /**
     * 商户订单号。
     */
    @SerializedName("out_trade_no")
    @Expose(serialize = false)
    public String outTradeNo;

    /**
     * 商户号。
     * 如果未传，则服务层会自动回退使用已初始化的商户号。
     */
    @SerializedName("mchid")
    @Expose(serialize = false)
    public String mchid;
}
