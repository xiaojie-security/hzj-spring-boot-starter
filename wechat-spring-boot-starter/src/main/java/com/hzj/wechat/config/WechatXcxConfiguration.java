package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.impl.DefaultWechatAccessTokenService;
import com.hzj.wechat.core.qrcode.WechatXcxQRCodeService;
import com.hzj.wechat.core.qrcode.impl.DefaultWechatXcxQRCodeService;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import com.hzj.wechat.provider.wechat.qrcode.WechatQrCodeConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序能力自动装配配置。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "wechat.xcx", name = "enable", havingValue = "true", matchIfMissing = true)
public class WechatXcxConfiguration {

    /**
     * 当小程序能力单独启用时，补充注册接口调用凭据服务。
     *
     * @param provider 微信商户配置提供者
     * @return 微信接口调用凭据服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatAccessTokenService.class)
    public WechatAccessTokenService xcxWechatAccessTokenService(WechatAccessConfigProvider provider) {
        return new DefaultWechatAccessTokenService(provider);
    }

    /**
     * 注册微信小程序二维码服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @return 微信小程序二维码服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxQRCodeService.class)
    public WechatXcxQRCodeService wechatXcxQRCodeService(WechatAccessTokenService accessTokenService,
                                                         WechatQrCodeConfigProvider provider) {
        return new DefaultWechatXcxQRCodeService(accessTokenService, provider, new OkHttpClient.Builder().build());
    }
}
