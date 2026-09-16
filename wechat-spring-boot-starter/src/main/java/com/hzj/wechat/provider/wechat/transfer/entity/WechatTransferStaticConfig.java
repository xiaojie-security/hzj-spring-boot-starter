package com.hzj.wechat.provider.wechat.transfer.entity;

import com.hzj.wechat.provider.wechat.payment.entity.WechatPaymentStaticConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信商家转账启动期静态配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatTransferStaticConfig extends WechatPaymentStaticConfig {
}
