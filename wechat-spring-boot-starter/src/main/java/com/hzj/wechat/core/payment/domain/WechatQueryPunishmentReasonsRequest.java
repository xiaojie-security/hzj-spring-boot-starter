package com.hzj.wechat.core.payment.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询商户管控原因请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryPunishmentReasonsRequest extends WechatVirtualPaymentRequest {
}
