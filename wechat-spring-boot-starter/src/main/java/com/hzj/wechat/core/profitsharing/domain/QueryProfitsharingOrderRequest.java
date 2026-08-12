package com.hzj.wechat.core.profitsharing.domain;

/**
 * 查询分账结果请求参数。
 */
public class QueryProfitsharingOrderRequest extends WechatProfitsharingApiRequest {
    /**
     * 查询分账结果请求参数。
     */
    public QueryProfitsharingOrderRequest() {
        requestPath = "/v3/profitsharing/orders/{out_order_no}";
        requestMethod = com.hzj.wechat.core.enums.WechatHttpMethod.GET;
    }
    /**
     * 商户分账单号。
     */
    public String outOrderNo;

    /**
     * 微信支付订单号。
     */
    public String transactionId;
}
