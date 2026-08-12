package com.hzj.wechat.core.profitsharing.domain;

/**
 * 查询剩余待分金额请求参数。
 */
public class QueryProfitsharingAmountRequest extends WechatProfitsharingApiRequest {
    /**
     * 查询剩余待分金额请求参数。
     */
    public QueryProfitsharingAmountRequest() {
        requestPath = "/v3/profitsharing/transactions/{transaction_id}/amounts";
        requestMethod = com.hzj.wechat.core.enums.WechatHttpMethod.GET;
    }
    /**
     * 微信支付订单号。
     */
    public String transactionId;
}
