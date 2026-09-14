package com.hzj.wechat.config;

import com.hzj.wechat.core.oauth2.WechatWebpageOAuth2Service;
import com.hzj.wechat.core.oauth2.impl.DefaultWechatWebpageOAuth2Service;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信网页授权自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link WechatAccessConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(WechatAccessConfigProvider.class)
public class WechatOAuth2Configuration {

    @Bean
    @ConditionalOnMissingBean(WechatWebpageOAuth2Service.class)
    public WechatWebpageOAuth2Service wechatWebpageOAuth2Service(WechatAccessConfigProvider provider) {
        return new DefaultWechatWebpageOAuth2Service(provider);
    }

}
