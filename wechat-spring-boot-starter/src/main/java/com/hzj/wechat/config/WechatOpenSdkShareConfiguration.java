package com.hzj.wechat.config;

import com.hzj.wechat.core.mobile.WechatOpenSdkShareSignatureService;
import com.hzj.wechat.core.mobile.impl.DefaultWechatOpenSdkShareSignatureService;
import com.hzj.wechat.provider.wechat.mobile.share.WechatOpenSdkShareConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 微信 OpenSDK 分享能力自动装配配置。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "wechat.mobile.share", name = "enable", havingValue = "true", matchIfMissing = true)
public class WechatOpenSdkShareConfiguration {

    /**
     * 注册微信 OpenSDK 分享签名服务。
     *
     * @param configProvider 微信 OpenSDK 分享动态配置提供者
     * @return 微信 OpenSDK 分享签名服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatOpenSdkShareSignatureService.class)
    public WechatOpenSdkShareSignatureService wechatOpenSdkShareSignatureService(
            WechatOpenSdkShareConfigProvider configProvider) {
        return new DefaultWechatOpenSdkShareSignatureService(configProvider);
    }
}
