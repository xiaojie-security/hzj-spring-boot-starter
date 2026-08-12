package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询订阅协议请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQuerySubscribeContractRequest extends WechatVirtualPaymentRequest {
    public WechatQuerySubscribeContractRequest() { super(WechatVirtualPaymentApi.QUERY_SUBSCRIBE_CONTRACT); }

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 订阅道具 ID。
     */
    @SerializedName("product_id")
    private String productId;

    /**
     * 商户侧协议号。
     */
    @SerializedName("out_contract_code")
    private String outContractCode;
}
