package com.hzj.wechat.core.profitsharing.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 解冻剩余资金请求参数。
 */
public class UnfreezeProfitsharingOrderRequest extends WechatProfitsharingApiRequest {
    /**
     * 解冻剩余资金请求参数。
     */
    public UnfreezeProfitsharingOrderRequest() {
        requestPath = "/v3/profitsharing/orders/unfreeze";
        requestMethod = com.hzj.wechat.core.enums.WechatHttpMethod.POST;
    }
    /**
     * 微信支付订单号。
     */
    @SerializedName("transaction_id")
    public String transactionId;

    /**
     * 商户分账单号。
     */
    @SerializedName("out_order_no")
    public String outOrderNo;

    /**
     * 解冻原因描述。
     */
    @SerializedName("description")
    public String description;
}
