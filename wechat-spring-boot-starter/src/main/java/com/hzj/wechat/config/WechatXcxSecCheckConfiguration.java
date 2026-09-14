package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.xcx.sec_check.WechatXcxSecCheckService;
import com.hzj.wechat.core.xcx.sec_check.impl.DefaultWechatXcxSecCheckService;
import com.hzj.wechat.provider.wechat.sec_check.WechatSecCheckConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序内容安全自动装配配置。
 * <p>
 * 仅当使用方同时提供了 {@link WechatAccessTokenService} 与
 * {@link WechatSecCheckConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean({WechatAccessTokenService.class, WechatSecCheckConfigProvider.class})
public class WechatXcxSecCheckConfiguration {

    /**
     * 注册微信小程序内容安全服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信内容安全配置提供者
     * @return 微信小程序内容安全服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxSecCheckService.class)
    public WechatXcxSecCheckService wechatXcxSecCheckService(WechatAccessTokenService accessTokenService,
                                                             WechatSecCheckConfigProvider provider) {
        return new DefaultWechatXcxSecCheckService(accessTokenService, provider, new OkHttpClient.Builder().build());
    }
}
