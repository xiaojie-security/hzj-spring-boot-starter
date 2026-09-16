package com.hzj.wechat.config;

import com.hzj.wechat.core.xcx.oauth.WechatXcxOAuthService;
import com.hzj.wechat.core.xcx.oauth.impl.DefaultWechatXcxOAuthService;
import com.hzj.wechat.provider.wechat.access.WechatAccessStaticConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序登录（code2Session）自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link WechatAccessStaticConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(WechatAccessStaticConfigProvider.class)
public class WechatXcxOAuthConfiguration {

    /**
     * 注册微信小程序登录服务。
     *
     * @param provider 微信接口调用凭据配置提供者
     * @return 微信小程序登录服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxOAuthService.class)
    public WechatXcxOAuthService wechatXcxOAuthService(WechatAccessStaticConfigProvider provider) {
        return new DefaultWechatXcxOAuthService(provider);
    }
}
