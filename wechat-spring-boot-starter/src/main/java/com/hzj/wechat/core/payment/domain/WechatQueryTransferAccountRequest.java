package com.hzj.wechat.core.payment.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询广告金充值账户请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryTransferAccountRequest extends WechatVirtualPaymentRequest {
}
