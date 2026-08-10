package com.hzj.wechat.config;

import com.hzj.wechat.core.profitsharing.service.WechatProfitsharingService;
import com.hzj.wechat.core.profitsharing.service.impl.DefaultWechatProfitsharingService;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "wechat.profitsharing", name = "enable", havingValue = "true", matchIfMissing = true)
public class WechatProfitsharingConfiguration {


    @Bean
    @ConditionalOnMissingBean(WechatProfitsharingService.class)
    public WechatProfitsharingService wechatProfitsharingService(WechatPaymentConfigProvider provider){
        return new DefaultWechatProfitsharingService(provider);
    }
}
