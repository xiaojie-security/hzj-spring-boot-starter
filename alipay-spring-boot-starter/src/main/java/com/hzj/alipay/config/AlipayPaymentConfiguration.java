package com.hzj.alipay.config;

import com.hzj.alipay.core.payment.AlipayPaymentService;
import com.hzj.alipay.core.payment.impl.DefaultAlipayPaymentService;
import com.hzj.alipay.core.payment.AlipayPaymentCallbackService;
import com.hzj.alipay.core.payment.impl.DefaultAlipayPaymentCallbackService;
import com.hzj.alipay.provider.alipay.payment.AlipayPaymentConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 支付宝支付自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link AlipayPaymentConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(AlipayPaymentConfigProvider.class)
public class AlipayPaymentConfiguration {

    /**
     * 装配支付宝支付服务。
     *
     * @return 支付服务
     */
    @Bean
    @ConditionalOnMissingBean(AlipayPaymentService.class)
    public AlipayPaymentService alipayPaymentService(AlipayPaymentConfigProvider provider) {
        return new DefaultAlipayPaymentService(provider);
    }

    /**
     * 装配支付宝支付回调服务。
     *
     * @return 支付回调服务
     */
    @Bean
    @ConditionalOnMissingBean(AlipayPaymentCallbackService.class)
    public AlipayPaymentCallbackService alipayPaymentCallbackService(AlipayPaymentConfigProvider provider) {
        return new DefaultAlipayPaymentCallbackService(provider);
    }
}
