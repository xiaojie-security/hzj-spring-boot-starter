package com.hzj.wechat.core.profitsharing.domain;

/**
 * 查询分账回退结果请求参数。
 */
public class QueryProfitsharingReturnOrderRequest extends WechatProfitsharingApiRequest {
    /**
     * 查询分账回退结果请求参数。
     */
    public QueryProfitsharingReturnOrderRequest() {
        requestPath = "/v3/profitsharing/return-orders/{out_return_no}";
        requestMethod = com.hzj.wechat.core.enums.WechatHttpMethod.GET;
    }
    /**
     * 商户分账回退单号。
     */
    public String outReturnNo;
}
