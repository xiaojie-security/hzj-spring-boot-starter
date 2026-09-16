package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.mobile.launch.WechatH5LaunchAppService;
import com.hzj.wechat.core.mobile.launch.impl.DefaultWechatH5LaunchAppService;
import com.hzj.wechat.provider.wechat.mobile.launch.WechatH5LaunchAppRuntimeConfigProvider;
import com.hzj.wechat.provider.wechat.mobile.launch.WechatH5LaunchAppStaticConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信 H5 JS-SDK 签名与 Launch App 自动装配配置。
 * <p>
 * 仅当使用方同时提供了 {@link WechatAccessTokenService} 与
 * {@link WechatH5LaunchAppStaticConfigProvider} 与
 * {@link WechatH5LaunchAppRuntimeConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean({WechatAccessTokenService.class, WechatH5LaunchAppStaticConfigProvider.class,
        WechatH5LaunchAppRuntimeConfigProvider.class})
public class WechatH5LaunchAppConfiguration {

    /**
     * 注册微信 H5 JS-SDK 签名与 Launch App 场景服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param staticConfigProvider 微信 H5 拉起 App 启动期静态配置提供者
     * @param runtimeConfigProvider 微信 H5 拉起 App 运行时业务配置提供者
     * @return 微信 H5 JS-SDK 签名与 Launch App 场景服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatH5LaunchAppService.class)
    public WechatH5LaunchAppService wechatH5LaunchAppService(WechatAccessTokenService accessTokenService,
                                                             WechatH5LaunchAppStaticConfigProvider staticConfigProvider,
                                                             WechatH5LaunchAppRuntimeConfigProvider runtimeConfigProvider) {
        return new DefaultWechatH5LaunchAppService(accessTokenService, staticConfigProvider, runtimeConfigProvider);
    }
}
