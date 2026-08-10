package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 取消订阅协议请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatCancelSubscribeContractRequest extends WechatVirtualPaymentRequest {

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 解约原因。
     */
    @SerializedName("termination_reason")
    private String terminationReason;

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
