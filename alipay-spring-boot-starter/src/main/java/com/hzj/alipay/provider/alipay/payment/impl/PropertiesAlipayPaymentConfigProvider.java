package com.hzj.alipay.provider.alipay.payment.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.alipay.properties.AlipayProperties;
import com.hzj.alipay.provider.alipay.payment.AlipayPaymentConfigProvider;
import com.hzj.alipay.provider.alipay.payment.entity.AlipayPaymentConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的支付宝支付配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAlipayPaymentConfigProvider implements AlipayPaymentConfigProvider {

    private final AlipayProperties properties;

    @Override
    public AlipayPaymentConfig getConfig() {
        return BeanUtil.copyProperties(properties, AlipayPaymentConfig.class);
    }
}
