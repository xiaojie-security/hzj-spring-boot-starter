package com.hzj.wechat.config;

import com.hzj.wechat.core.profitsharing.service.WechatProfitsharingService;
import com.hzj.wechat.core.profitsharing.service.impl.DefaultWechatProfitsharingService;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信分账自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link WechatPaymentConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(WechatPaymentConfigProvider.class)
public class WechatProfitsharingConfiguration {


    @Bean
    @ConditionalOnMissingBean(WechatProfitsharingService.class)
    public WechatProfitsharingService wechatProfitsharingService(WechatPaymentConfigProvider provider){
        return new DefaultWechatProfitsharingService(provider);
    }
}
