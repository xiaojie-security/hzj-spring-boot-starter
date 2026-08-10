package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 扣减用户代币请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatCurrencyPayRequest extends WechatVirtualPaymentRequest {

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 用户 IP 地址。
     */
    @SerializedName("user_ip")
    private String userIp;

    /**
     * 扣减代币数量。
     */
    private Long amount;

    /**
     * 商户订单号。
     */
    @SerializedName("order_id")
    private String orderId;

    /**
     * 物品信息 JSON 字符串。
     */
    private String payitem;

    /**
     * 订单备注。
     */
    private String remark;
}
