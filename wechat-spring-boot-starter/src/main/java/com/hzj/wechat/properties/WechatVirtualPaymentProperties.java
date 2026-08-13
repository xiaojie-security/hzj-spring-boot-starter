package com.hzj.wechat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信虚拟支付配置属性。
 */
@Data
@ConfigurationProperties(prefix = "wechat.virtual-payment")
public class WechatVirtualPaymentProperties {

    /** 米大师侧申请的应用 ID。 */
    private String offerId;
    /** 虚拟支付环境。 */
    private Integer env = 0;
    /** 虚拟支付币种。 */
    private String currencyType = "CNY";
}
