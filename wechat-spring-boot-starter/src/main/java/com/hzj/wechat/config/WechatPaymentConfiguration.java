package com.hzj.wechat.config;

import com.hzj.wechat.core.payment.service.WechatPaymentService;
import com.hzj.wechat.core.payment.service.WechatPaymentCallbackService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatPaymentCallbackService;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatPaymentService;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 微信支付 Starter 自动装配配置。
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "wechat.payment", name = "enable", havingValue = "true", matchIfMissing = true)
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
