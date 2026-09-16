package com.hzj.alipay.config;

import com.hzj.alipay.core.payment.AlipayPaymentService;
import com.hzj.alipay.core.payment.impl.DefaultAlipayPaymentService;
import com.hzj.alipay.core.payment.AlipayPaymentCallbackService;
import com.hzj.alipay.core.payment.impl.DefaultAlipayPaymentCallbackService;
import com.hzj.alipay.provider.alipay.payment.AlipayPaymentRuntimeConfigProvider;
import com.hzj.alipay.provider.alipay.payment.AlipayPaymentStaticConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 支付宝支付自动装配配置。
 * <p>
 * 仅当使用方同时提供了支付宝支付静态和运行时配置 Provider 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean({AlipayPaymentStaticConfigProvider.class, AlipayPaymentRuntimeConfigProvider.class})
public class AlipayPaymentConfiguration {

    /**
     * 装配支付宝支付服务。
     *
     * @param staticConfigProvider 支付宝支付启动期静态配置提供者
     * @param runtimeConfigProvider 支付宝支付运行时业务配置提供者
     * @return 支付服务
     */
    @Bean
    @ConditionalOnMissingBean(AlipayPaymentService.class)
    public AlipayPaymentService alipayPaymentService(AlipayPaymentStaticConfigProvider staticConfigProvider,
                                                     AlipayPaymentRuntimeConfigProvider runtimeConfigProvider) {
        return new DefaultAlipayPaymentService(staticConfigProvider, runtimeConfigProvider);
    }

    /**
     * 装配支付宝支付回调服务。
     *
     * @param staticConfigProvider 支付宝支付启动期静态配置提供者
     * @return 支付回调服务
     */
    @Bean
    @ConditionalOnMissingBean(AlipayPaymentCallbackService.class)
    public AlipayPaymentCallbackService alipayPaymentCallbackService(
            AlipayPaymentStaticConfigProvider staticConfigProvider) {
        return new DefaultAlipayPaymentCallbackService(staticConfigProvider);
    }
}
