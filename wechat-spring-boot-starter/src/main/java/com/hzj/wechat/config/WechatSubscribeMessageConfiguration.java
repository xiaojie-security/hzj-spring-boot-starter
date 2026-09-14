package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.xcx.subscribe_message.WechatXcxSubscribeMessageService;
import com.hzj.wechat.core.xcx.subscribe_message.impl.DefaultWechatXcxSubscribeMessageService;
import com.hzj.wechat.provider.wechat.subscribe_message.WechatSubscribeMessageConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序订阅消息自动装配配置。
 * <p>
 * 仅当使用方同时提供了 {@link WechatAccessTokenService} 与
 * {@link WechatSubscribeMessageConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean({WechatAccessTokenService.class, WechatSubscribeMessageConfigProvider.class})
public class WechatSubscribeMessageConfiguration {

    /**
     * 注册微信订阅消息服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信订阅消息配置提供者
     * @return 微信订阅消息服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxSubscribeMessageService.class)
    public WechatXcxSubscribeMessageService wechatXcxSubscribeMessageService(
            WechatAccessTokenService accessTokenService,
            WechatSubscribeMessageConfigProvider provider) {
        return new DefaultWechatXcxSubscribeMessageService(
                accessTokenService, provider, new OkHttpClient.Builder().build());
    }
}
