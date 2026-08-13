package com.hzj.wechat.provider.wechat.payment.impl;

import com.hzj.wechat.properties.WechatPaymentProperties;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.payment.entity.WechatPaymentConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的微信支付配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesWechatPaymentConfigProvider implements WechatPaymentConfigProvider {

    /** 微信支付配置属性。 */
    private final WechatPaymentProperties properties;

    /**
     * 获取微信支付配置。
     *
     * @return 微信支付配置
     */
    @Override
    public WechatPaymentConfig getConfig() {
        WechatPaymentConfig config = new WechatPaymentConfig();
        config.setMchid(properties.getMchid());
        config.setAppid(properties.getAppid());
        config.setAppSecret(properties.getAppSecret());
        config.setPrivateKey(isBlank(properties.getPrivateKeyPath()) ? null
                : WechatPayUtils.loadPrivateKeyFromPath(properties.getPrivateKeyPath()));
        config.setCertificateSerialNo(properties.getCertificateSerialNo());
        config.setWechatPayPublicKey(isBlank(properties.getWechatPayPublicKeyPath()) ? null
                : WechatPayUtils.loadPublicKeyFromPath(properties.getWechatPayPublicKeyPath()));
        config.setWechatPayPublicKeyId(properties.getWechatPayPublicKeyId());
        config.setApiV3Secret(properties.getApiV3Secret());
        config.setPaymentNotifyUrl(properties.getPaymentNotifyUrl());
        return config;
    }

    /**
     * 判断字符串是否为空。
     *
     * @param value 字符串
     * @return 是否为空
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
