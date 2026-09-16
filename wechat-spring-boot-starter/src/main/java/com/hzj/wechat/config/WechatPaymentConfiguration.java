package com.hzj.wechat.config;

import com.hzj.wechat.core.payment.service.WechatPaymentService;
import com.hzj.wechat.core.payment.service.WechatPaymentCallbackService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatPaymentCallbackService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatPaymentService;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentRuntimeConfigProvider;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentStaticConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信支付自动装配配置。
 * <p>
 * 仅当使用方同时提供了微信支付静态和运行时配置 Provider 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean({WechatPaymentStaticConfigProvider.class, WechatPaymentRuntimeConfigProvider.class})
public class WechatPaymentConfiguration {


    @Bean
    @ConditionalOnMissingBean(WechatPaymentService.class)
    public WechatPaymentService wechatPaymentService(WechatPaymentStaticConfigProvider staticConfigProvider,
                                                     WechatPaymentRuntimeConfigProvider runtimeConfigProvider){
        return new DefaultWechatPaymentService(staticConfigProvider, runtimeConfigProvider);
    }

    @Bean
    @ConditionalOnMissingBean(WechatPaymentCallbackService.class)
    public WechatPaymentCallbackService wechatPaymentCallbackService(
            WechatPaymentStaticConfigProvider staticConfigProvider) {
        return new DefaultWechatPaymentCallbackService(staticConfigProvider);
    }
}
