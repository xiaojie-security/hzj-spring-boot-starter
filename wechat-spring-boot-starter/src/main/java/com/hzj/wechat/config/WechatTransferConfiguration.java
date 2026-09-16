package com.hzj.wechat.config;

import com.hzj.wechat.core.transfer.service.WechatAutoApprovalResultNotifyService;
import com.hzj.wechat.core.transfer.service.WechatTransferCallbackService;
import com.hzj.wechat.core.transfer.service.WechatTransferService;
import com.hzj.wechat.core.transfer.service.impl.DefaultWechatAutoApprovalResultNotifyService;
import com.hzj.wechat.core.transfer.service.impl.DefaultWechatTransferCallbackService;
import com.hzj.wechat.core.transfer.service.impl.DefaultWechatTransferService;
import com.hzj.wechat.provider.wechat.transfer.WechatTransferRuntimeConfigProvider;
import com.hzj.wechat.provider.wechat.transfer.WechatTransferStaticConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


/**
 * 微信商家转账自动装配配置。
 * <p>
 * 仅当使用方同时提供了微信商家转账静态和运行时配置 Provider 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean({WechatTransferStaticConfigProvider.class, WechatTransferRuntimeConfigProvider.class})
public class WechatTransferConfiguration {


    @Bean
    @ConditionalOnMissingBean(WechatTransferService.class)
    public WechatTransferService wechatTransferService(WechatTransferStaticConfigProvider staticConfigProvider,
                                                       WechatTransferRuntimeConfigProvider runtimeConfigProvider){
        return new DefaultWechatTransferService(staticConfigProvider, runtimeConfigProvider);
    }

    @Bean
    @ConditionalOnMissingBean(WechatAutoApprovalResultNotifyService.class)
    public WechatAutoApprovalResultNotifyService wechatAutoApprovalResultNotifyService(
            WechatTransferStaticConfigProvider staticConfigProvider) {
        return new DefaultWechatAutoApprovalResultNotifyService(staticConfigProvider);
    }

    @Bean
    @ConditionalOnMissingBean(WechatTransferCallbackService.class)
    public WechatTransferCallbackService wechatTransferCallbackService(
            WechatTransferStaticConfigProvider staticConfigProvider) {
        return new DefaultWechatTransferCallbackService(staticConfigProvider);
    }
}
