package com.hzj.alipay.provider.alipay.transfer.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.alipay.properties.AlipayProperties;
import com.hzj.alipay.provider.alipay.transfer.AlipayTransferConfigProvider;
import com.hzj.alipay.provider.alipay.transfer.entity.AlipayTransferConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的支付宝转账配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAlipayTransferConfigProvider implements AlipayTransferConfigProvider {

    private final AlipayProperties properties;

    @Override
    public AlipayTransferConfig getConfig() {
        return BeanUtil.copyProperties(properties, AlipayTransferConfig.class);
    }
}
