package com.hzj.wechat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信接口调用凭据配置属性。
 */
@Data
@ConfigurationProperties(prefix = "wechat.access")
public class WechatAccessProperties {

    /** 微信应用唯一标识。 */
    private String appid;

    /** 微信应用密钥。 */
    private String secret;
}
