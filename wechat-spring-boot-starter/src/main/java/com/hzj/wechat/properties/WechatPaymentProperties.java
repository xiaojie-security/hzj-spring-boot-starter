package com.hzj.wechat.properties;

import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信支付配置属性。
 */
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "wechat.payment")
public class WechatPaymentProperties extends WechatMerchantProperties {
}
