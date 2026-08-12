package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询虚拟支付提现单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryWithdrawOrderRequest extends WechatVirtualPaymentRequest {
    public WechatQueryWithdrawOrderRequest() { super(WechatVirtualPaymentApi.QUERY_WITHDRAW_ORDER); }

    /**
     * 商户提现单号。
     */
    @SerializedName("withdraw_no")
    private String withdrawNo;
}
