package com.hzj.wechat.provider.wechat.virtual.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.wechat.properties.WechatVirtualPaymentProperties;
import com.hzj.wechat.provider.wechat.virtual.WechatVirtualPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.virtual.entity.WechatVirtualPaymentConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的微信虚拟支付配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesWechatVirtualPaymentConfigProvider implements WechatVirtualPaymentConfigProvider {

    /** 微信虚拟支付配置属性。 */
    private final WechatVirtualPaymentProperties properties;

    /**
     * 获取微信虚拟支付配置。
     *
     * @return 微信虚拟支付配置
     */
    @Override
    public WechatVirtualPaymentConfig getConfig() {
        return BeanUtil.copyProperties(properties, WechatVirtualPaymentConfig.class);
    }
}
