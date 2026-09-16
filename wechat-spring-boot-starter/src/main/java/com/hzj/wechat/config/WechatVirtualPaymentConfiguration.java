package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.payment.service.WechatVirtualPaymentService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatVirtualPaymentService;
import com.hzj.wechat.provider.wechat.virtual.WechatVirtualPaymentStaticConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信虚拟支付自动装配配置。
 * <p>
 * 仅当使用方同时提供了 {@link WechatAccessTokenService} 与
 * {@link WechatVirtualPaymentStaticConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean({WechatAccessTokenService.class, WechatVirtualPaymentStaticConfigProvider.class})
public class WechatVirtualPaymentConfiguration {

    /**
     * 注册微信虚拟支付服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信虚拟支付配置提供者
     * @return 微信虚拟支付服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatVirtualPaymentService.class)
    public WechatVirtualPaymentService wechatVirtualPaymentService(
            WechatAccessTokenService accessTokenService, WechatVirtualPaymentStaticConfigProvider provider) {
        return new DefaultWechatVirtualPaymentService(accessTokenService, provider,
                new OkHttpClient.Builder().build());
    }
}
