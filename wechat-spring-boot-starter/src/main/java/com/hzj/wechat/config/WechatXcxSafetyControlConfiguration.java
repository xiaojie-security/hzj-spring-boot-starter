package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.xcx.safety_control.WechatXcxSafetyControlService;
import com.hzj.wechat.core.xcx.safety_control.impl.DefaultWechatXcxSafetyControlService;
import com.hzj.wechat.provider.wechat.safety_control.WechatSafetyControlConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序安全风控自动装配配置。
 * <p>
 * 仅当使用方同时提供了 {@link WechatAccessTokenService} 与
 * {@link WechatSafetyControlConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean({WechatAccessTokenService.class, WechatSafetyControlConfigProvider.class})
public class WechatXcxSafetyControlConfiguration {

    /**
     * 注册微信小程序安全风控服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信安全风控配置提供者
     * @return 微信小程序安全风控服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxSafetyControlService.class)
    public WechatXcxSafetyControlService wechatXcxSafetyControlService(
            WechatAccessTokenService accessTokenService,
            WechatSafetyControlConfigProvider provider) {
        return new DefaultWechatXcxSafetyControlService(
                accessTokenService, provider, new OkHttpClient.Builder().build());
    }
}
