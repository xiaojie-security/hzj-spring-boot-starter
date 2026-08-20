package com.hzj.wechat.provider.wechat.mobile.share.impl;

import com.hzj.wechat.properties.WechatOpenSdkShareProperties;
import com.hzj.wechat.provider.wechat.mobile.share.WechatOpenSdkShareConfigProvider;
import com.hzj.wechat.provider.wechat.mobile.share.entity.WechatOpenSdkShareConfig;
import com.hzj.wechat.utils.WechatOpenSdkSignatureUtils;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的微信 OpenSDK 分享配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesWechatOpenSdkShareConfigProvider implements WechatOpenSdkShareConfigProvider {

    /** 微信 OpenSDK 分享配置属性。 */
    private final WechatOpenSdkShareProperties properties;

    /**
     * 获取微信 OpenSDK 分享配置。
     *
     * @return 微信 OpenSDK 分享配置
     */
    @Override
    public WechatOpenSdkShareConfig getConfig() {
        WechatOpenSdkShareConfig config = new WechatOpenSdkShareConfig();
        config.setAppid(properties.getAppid());
        config.setSignatureAlgorithm(properties.getSignatureAlgorithm());
        if (properties.getPrivateKeyPath() != null && !properties.getPrivateKeyPath().isBlank()) {
            config.setPrivateKey(WechatOpenSdkSignatureUtils.loadPrivateKeyFromPath(
                    properties.getPrivateKeyPath(), properties.getSignatureAlgorithm()));
        }
        return config;
    }
}
