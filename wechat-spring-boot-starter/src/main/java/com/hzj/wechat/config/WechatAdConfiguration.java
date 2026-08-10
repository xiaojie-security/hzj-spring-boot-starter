package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.impl.DefaultWechatAccessTokenService;
import com.hzj.wechat.core.ad.WechatAdDataService;
import com.hzj.wechat.core.ad.impl.DefaultWechatAdDataService;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序广告数据自动装配配置。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "wechat.ad", name = "enable", havingValue = "true", matchIfMissing = true)
public class WechatAdConfiguration {

    /**
     * 广告能力单独启用时，补充注册接口调用凭据服务。
     *
     * @param provider 微信商户配置提供者
     * @return 微信接口调用凭据服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatAccessTokenService.class)
    public WechatAccessTokenService adWechatAccessTokenService(WechatAccessConfigProvider provider) {
        return new DefaultWechatAccessTokenService(provider);
    }

    /**
     * 注册微信广告数据服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @return 微信广告数据服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatAdDataService.class)
    public WechatAdDataService wechatAdDataService(WechatAccessTokenService accessTokenService) {
        return new DefaultWechatAdDataService(accessTokenService, new OkHttpClient.Builder().build());
    }
}
