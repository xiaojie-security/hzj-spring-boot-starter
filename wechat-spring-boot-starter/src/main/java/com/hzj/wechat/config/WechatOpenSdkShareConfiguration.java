package com.hzj.wechat.config;

import com.hzj.wechat.core.mobile.share.WechatOpenSdkShareSignatureService;
import com.hzj.wechat.core.mobile.share.impl.DefaultWechatOpenSdkShareSignatureService;
import com.hzj.wechat.provider.wechat.mobile.share.WechatOpenSdkShareConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信 OpenSDK 分享能力自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link WechatOpenSdkShareConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(WechatOpenSdkShareConfigProvider.class)
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
