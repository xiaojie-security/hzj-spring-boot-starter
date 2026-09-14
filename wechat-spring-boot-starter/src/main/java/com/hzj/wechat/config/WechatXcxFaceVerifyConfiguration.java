package com.hzj.wechat.config;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.xcx.face.WechatXcxFaceVerifyService;
import com.hzj.wechat.core.xcx.face.impl.DefaultWechatXcxFaceVerifyService;
import com.hzj.wechat.provider.wechat.face.WechatFaceVerifyConfigProvider;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 微信小程序人脸验证自动装配配置。
 * <p>
 * 仅当使用方同时提供了 {@link WechatAccessTokenService} 与
 * {@link WechatFaceVerifyConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration(after = WechatAccessConfiguration.class)
@ConditionalOnBean({WechatAccessTokenService.class, WechatFaceVerifyConfigProvider.class})
public class WechatXcxFaceVerifyConfiguration {

    /**
     * 注册微信小程序人脸验证服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信人脸核身配置提供者
     * @return 微信小程序人脸验证服务
     */
    @Bean
    @ConditionalOnMissingBean(WechatXcxFaceVerifyService.class)
    public WechatXcxFaceVerifyService wechatXcxFaceVerifyService(WechatAccessTokenService accessTokenService,
                                                                 WechatFaceVerifyConfigProvider provider) {
        return new DefaultWechatXcxFaceVerifyService(accessTokenService, provider, new OkHttpClient.Builder().build());
    }
}
