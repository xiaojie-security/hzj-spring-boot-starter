package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.xcx.ad.WechatXcxAdDataService;
import com.hzj.wechat.core.xcx.ad.impl.DefaultWechatXcxAdDataService;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序广告数据自动装配配置。
 * <p>
 * 仅当使用方提供了 {@link WechatAccessTokenService} Bean（可由
 * {@link WechatAccessConfiguration} 装配，也可自行提供）时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean(WechatAccessTokenService.class)
public class WechatAdConfiguration {

    /**
     * 注册微信广告数据服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @return 微信广告数据服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxAdDataService.class)
    public WechatXcxAdDataService wechatAdDataService(WechatAccessTokenService accessTokenService) {
        return new DefaultWechatXcxAdDataService(accessTokenService, new OkHttpClient.Builder().build());
    }
}
