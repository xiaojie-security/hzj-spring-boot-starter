package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建虚拟支付提现单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatCreateWithdrawOrderRequest extends WechatVirtualPaymentRequest {
    public WechatCreateWithdrawOrderRequest() { super(WechatVirtualPaymentApi.CREATE_WITHDRAW_ORDER); }

    /**
     * 商户提现单号。
     */
    @SerializedName("withdraw_no")
    private String withdrawNo;

    /**
     * 提现金额，单位元；不传表示全额提现。
     */
    @SerializedName("withdraw_amount")
    private String withdrawAmount;
}
