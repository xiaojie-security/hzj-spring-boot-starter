package com.hzj.alipay.config;

import com.hzj.alipay.core.oauth2.AliPayOAuth2Service;
import com.hzj.alipay.core.oauth2.impl.DefaultAliPayOAuth2Service;
import com.hzj.alipay.provider.alipay.oauth2.AlipayOAuth2StaticConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 支付宝 OAuth2 自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link AlipayOAuth2StaticConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(AlipayOAuth2StaticConfigProvider.class)
public class AlipayOAuth2Configuration {

    /**
     * 装配支付宝 OAuth2 服务。
     *
     * @return OAuth2 服务
     */
    @Bean
    @ConditionalOnMissingBean(AliPayOAuth2Service.class)
    public AliPayOAuth2Service aliPayOAuth2Service(AlipayOAuth2StaticConfigProvider provider) {
        return new DefaultAliPayOAuth2Service(provider);
    }
}
