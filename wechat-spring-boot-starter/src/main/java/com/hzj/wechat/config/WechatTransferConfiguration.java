package com.hzj.wechat.config;

import com.hzj.wechat.core.transfer.service.WechatAutoApprovalResultNotifyService;
import com.hzj.wechat.core.transfer.service.WechatTransferCallbackService;
import com.hzj.wechat.core.transfer.service.WechatTransferService;
import com.hzj.wechat.core.transfer.service.impl.DefaultWechatAutoApprovalResultNotifyService;
import com.hzj.wechat.core.transfer.service.impl.DefaultWechatTransferCallbackService;
import com.hzj.wechat.core.transfer.service.impl.DefaultWechatTransferService;
import com.hzj.wechat.provider.wechat.transfer.WechatTransferConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "wechat.transfer", name = "enable", havingValue = "true", matchIfMissing = true)
public class WechatTransferConfiguration {


    @Bean
    @ConditionalOnMissingBean(WechatTransferService.class)
    public WechatTransferService wechatTransferService(WechatTransferConfigProvider provider){
        return new DefaultWechatTransferService(provider);
    }

    @Bean
    @ConditionalOnMissingBean(WechatAutoApprovalResultNotifyService.class)
    public WechatAutoApprovalResultNotifyService wechatAutoApprovalResultNotifyService(WechatTransferConfigProvider provider) {
        return new DefaultWechatAutoApprovalResultNotifyService(provider);
    }

    @Bean
    @ConditionalOnMissingBean(WechatTransferCallbackService.class)
    public WechatTransferCallbackService wechatTransferCallbackService(WechatTransferConfigProvider provider) {
        return new DefaultWechatTransferCallbackService(provider);
    }
}
