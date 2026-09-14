package com.hzj.wechat.config;

import com.hzj.wechat.core.payment.service.WechatPaymentService;
import com.hzj.wechat.core.payment.service.WechatPaymentCallbackService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatPaymentCallbackService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatPaymentService;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信支付自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link WechatPaymentConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(WechatPaymentConfigProvider.class)
public class WechatPaymentConfiguration {


    @Bean
    @ConditionalOnMissingBean(WechatPaymentService.class)
    public WechatPaymentService wechatPaymentService(WechatPaymentConfigProvider provider){
        return new DefaultWechatPaymentService(provider);
    }

    @Bean
    @ConditionalOnMissingBean(WechatPaymentCallbackService.class)
    public WechatPaymentCallbackService wechatPaymentCallbackService(WechatPaymentConfigProvider provider) {
        return new DefaultWechatPaymentCallbackService(provider);
    }
}
