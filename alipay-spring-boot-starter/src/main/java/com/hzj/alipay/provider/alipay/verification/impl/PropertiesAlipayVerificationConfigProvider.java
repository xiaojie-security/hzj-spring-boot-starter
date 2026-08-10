package com.hzj.alipay.provider.alipay.verification.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.alipay.properties.AlipayProperties;
import com.hzj.alipay.provider.alipay.verification.AlipayVerificationConfigProvider;
import com.hzj.alipay.provider.alipay.verification.entity.AlipayVerificationConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的支付宝核验配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAlipayVerificationConfigProvider implements AlipayVerificationConfigProvider {

    private final AlipayProperties properties;

    @Override
    public AlipayVerificationConfig getConfig() {
        return BeanUtil.copyProperties(properties, AlipayVerificationConfig.class);
    }
}
