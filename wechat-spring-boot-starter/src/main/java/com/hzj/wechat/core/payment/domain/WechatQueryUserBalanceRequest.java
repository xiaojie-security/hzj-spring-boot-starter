package com.hzj.wechat.core.payment.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

/**
 * 查询用户代币余额请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryUserBalanceRequest extends WechatVirtualPaymentRequest {
    public WechatQueryUserBalanceRequest() { super(WechatVirtualPaymentApi.QUERY_USER_BALANCE); }

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
