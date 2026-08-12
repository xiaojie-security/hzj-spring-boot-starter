package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询商户可提现余额请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryBizBalanceRequest extends WechatVirtualPaymentRequest {
    public WechatQueryBizBalanceRequest() { super(WechatVirtualPaymentApi.QUERY_BIZ_BALANCE); }
}
