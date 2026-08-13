package com.hzj.wechat.properties;

import com.hzj.wechat.core.qrcode.enums.WechatXcxEnvVersion;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信小程序二维码配置属性。
 */
@Data
@ConfigurationProperties(prefix = "wechat.qrcode")
public class WechatQrCodeProperties {

    /** 小程序二维码激活版本。 */
    private WechatXcxEnvVersion envVersion;
}
