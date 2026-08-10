package com.hzj.wechat.core.payment.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.google.gson.annotations.SerializedName;

/**
 * 查询用户代币余额请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryUserBalanceRequest extends WechatVirtualPaymentRequest {

    /**
     * 用户 openid。
     */
    private String openid;

    /**
     * 用户 IP 地址。
     */
    @SerializedName("user_ip")
    private String userIp;
}
