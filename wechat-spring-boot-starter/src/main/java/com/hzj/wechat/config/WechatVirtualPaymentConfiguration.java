package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.impl.DefaultWechatAccessTokenService;
import com.hzj.wechat.core.payment.service.WechatVirtualPaymentService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatVirtualPaymentService;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import com.hzj.wechat.provider.wechat.virtual.WechatVirtualPaymentConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 微信虚拟支付自动装配配置。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "wechat.virtual-payment", name = "enable",
        havingValue = "true", matchIfMissing = true)
public class WechatVirtualPaymentConfiguration {

    /**
     * 虚拟支付能力单独启用时，补充注册接口调用凭据服务。
     *
     * @param provider 微信配置提供者
     * @return 微信接口调用凭据服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatAccessTokenService.class)
    public WechatAccessTokenService virtualPaymentWechatAccessTokenService(WechatAccessConfigProvider provider) {
        return new DefaultWechatAccessTokenService(provider);
    }

    /**
     * 注册微信虚拟支付服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信配置提供者
     * @return 微信虚拟支付服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatVirtualPaymentService.class)
    public WechatVirtualPaymentService wechatVirtualPaymentService(
            WechatAccessTokenService accessTokenService, WechatVirtualPaymentConfigProvider provider) {
        return new DefaultWechatVirtualPaymentService(accessTokenService, provider,
                new OkHttpClient.Builder().build());
    }
}
