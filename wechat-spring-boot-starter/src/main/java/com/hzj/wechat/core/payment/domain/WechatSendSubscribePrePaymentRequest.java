package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订阅扣款预通知请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatSendSubscribePrePaymentRequest extends WechatVirtualPaymentRequest {

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 预扣款金额，单位为分。
     */
    @SerializedName("deduct_price")
    private Long deductPrice;

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
