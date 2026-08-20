package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.mobile.launch.WechatH5LaunchAppService;
import com.hzj.wechat.core.mobile.launch.impl.DefaultWechatH5LaunchAppService;
import com.hzj.wechat.provider.wechat.mobile.launch.WechatH5LaunchAppConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 微信 H5 JS-SDK 签名与 Launch App 自动装配配置。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean(WechatAccessTokenService.class)
@ConditionalOnProperty(prefix = "wechat.mobile.launch", name = "enable", havingValue = "true")
public class WechatH5LaunchAppConfiguration {

    /**
     * 注册微信 H5 JS-SDK 签名与 Launch App 场景服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param launchAppConfigProvider 微信 H5 拉起 App 动态配置提供者
     * @return 微信 H5 JS-SDK 签名与 Launch App 场景服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatH5LaunchAppService.class)
    public WechatH5LaunchAppService wechatH5LaunchAppService(WechatAccessTokenService accessTokenService,
                                                             WechatH5LaunchAppConfigProvider launchAppConfigProvider) {
        return new DefaultWechatH5LaunchAppService(accessTokenService, launchAppConfigProvider);
    }
}
