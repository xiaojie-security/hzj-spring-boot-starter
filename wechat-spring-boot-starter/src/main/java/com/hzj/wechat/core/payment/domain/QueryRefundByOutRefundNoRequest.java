package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 通过商户退款单号查询单笔退款请求参数。
 */
public class QueryRefundByOutRefundNoRequest extends WechatPaymentApiRequest {
    /**
     * 创建按商户退款单号查询退款请求参数。
     */
    public QueryRefundByOutRefundNoRequest() {
        requestPath = "/v3/refund/domestic/refunds/{out_refund_no}";
        requestMethod = WechatHttpMethod.GET;
    }
    /**
     * 商户退款单号。
     */
    @SerializedName("out_refund_no")
    @Expose(serialize = false)
    public String outRefundNo;
}
