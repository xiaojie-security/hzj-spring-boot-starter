package com.hzj.wechat.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信商家转账配置属性。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "wechat.transfer")
public class WechatTransferProperties extends WechatPaymentProperties {
}
