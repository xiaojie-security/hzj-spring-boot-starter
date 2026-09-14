package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.impl.DefaultWechatAccessTokenService;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信接口调用凭据自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link WechatAccessConfigProvider} Bean 时才会装配，
 * 不再提供任何基于配置文件的默认兜底实现。
 */
@AutoConfiguration
@ConditionalOnBean(WechatAccessConfigProvider.class)
public class WechatAccessConfiguration {

    /**
     * 注册微信接口调用凭据服务。
     *
     * @param provider 微信接口调用凭据配置提供者
     * @return 微信接口调用凭据服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatAccessTokenService.class)
    public WechatAccessTokenService wechatAccessTokenService(WechatAccessConfigProvider provider) {
        return new DefaultWechatAccessTokenService(provider);
    }
}
