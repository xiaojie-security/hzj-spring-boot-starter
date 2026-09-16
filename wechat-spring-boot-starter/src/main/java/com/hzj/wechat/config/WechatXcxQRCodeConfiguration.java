package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.xcx.qrcode.WechatXcxQRCodeService;
import com.hzj.wechat.core.xcx.qrcode.impl.DefaultWechatXcxQRCodeService;
import com.hzj.wechat.provider.wechat.qrcode.WechatQrCodeRuntimeConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序二维码自动装配配置。
 * <p>
 * 仅当使用方同时提供了 {@link WechatAccessTokenService} 与
 * {@link WechatQrCodeRuntimeConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean({WechatAccessTokenService.class, WechatQrCodeRuntimeConfigProvider.class})
public class WechatXcxQRCodeConfiguration {

    /**
     * 注册微信小程序二维码服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信小程序二维码配置提供者
     * @return 微信小程序二维码服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxQRCodeService.class)
    public WechatXcxQRCodeService wechatXcxQRCodeService(WechatAccessTokenService accessTokenService,
                                                         WechatQrCodeRuntimeConfigProvider provider) {
        return new DefaultWechatXcxQRCodeService(accessTokenService, provider, new OkHttpClient.Builder().build());
    }
}
