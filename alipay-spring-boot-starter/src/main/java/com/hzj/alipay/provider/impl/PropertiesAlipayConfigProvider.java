package com.hzj.alipay.provider.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.alipay.properties.AlipayProperties;
import com.hzj.alipay.provider.AlipayConfigProvider;
import com.hzj.alipay.provider.domain.AlipayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PropertiesAlipayConfigProvider implements AlipayConfigProvider {

    private final AlipayProperties properties;

    @Override
    public AlipayConfig getConfig() {
        return BeanUtil.copyProperties(properties, AlipayConfig.class);
    }
}
