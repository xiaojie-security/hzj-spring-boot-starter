package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.impl.DefaultWechatAccessTokenService;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 微信接口调用凭据自动装配配置。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "wechat.access", name = "enable", havingValue = "true", matchIfMissing = true)
public class WechatAccessConfiguration {

    /**
     * 注册微信接口调用凭据服务。
     *
     * @param provider 微信商户配置提供者
     * @return 微信接口调用凭据服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatAccessTokenService.class)
    public WechatAccessTokenService wechatAccessTokenService(WechatAccessConfigProvider provider) {
        return new DefaultWechatAccessTokenService(provider);
    }
}
