package com.hzj.wechat.provider.wechat.transfer.impl;

import com.hzj.wechat.properties.WechatTransferProperties;
import com.hzj.wechat.provider.wechat.payment.entity.WechatPaymentConfig;
import com.hzj.wechat.provider.wechat.transfer.WechatTransferConfigProvider;
import com.hzj.wechat.provider.wechat.transfer.entity.WechatTransferConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的微信商家转账配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesWechatTransferConfigProvider implements WechatTransferConfigProvider {

    /** 微信商家转账配置属性。 */
    private final WechatTransferProperties properties;

    /**
     * 获取微信商家转账配置。
     *
     * @return 微信商家转账配置
     */
    @Override
    public WechatTransferConfig getConfig() {
        WechatTransferConfig config = new WechatTransferConfig();
        copyPaymentConfig(config);
        config.setTransferNotifyUrl(properties.getTransferNotifyUrl());
        config.setAuthorizationNotifyUrl(properties.getAuthorizationNotifyUrl());
        return config;
    }

    /**
     * 复制支付基础配置。
     *
     * @param config 转账配置
     */
    private void copyPaymentConfig(WechatPaymentConfig config) {
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
