package com.hzj.wechat.config;

import com.hzj.wechat.core.oauth2.WechatWebpageOAuth2Service;
import com.hzj.wechat.core.oauth2.WechatXcxOAuth2Service;
import com.hzj.wechat.core.oauth2.impl.DefaultWechatWebpageOAuth2Service;
import com.hzj.wechat.core.oauth2.impl.DefaultWechatXcxOAuth2Service;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "wechat.oauth2", name = "enable", havingValue = "true", matchIfMissing = true)
public class WechatOAuth2Configuration {

    @Bean
    @ConditionalOnMissingBean(WechatWebpageOAuth2Service.class)
    public WechatWebpageOAuth2Service wechatWebpageOAuth2Service(WechatAccessConfigProvider provider) {
        return new DefaultWechatWebpageOAuth2Service(provider);
    }

    @Bean
    @ConditionalOnMissingBean(WechatXcxOAuth2Service.class)
    public WechatXcxOAuth2Service wechatXcxOAuth2Service(WechatAccessConfigProvider provider) {
        return new DefaultWechatXcxOAuth2Service(provider);
    }
}
