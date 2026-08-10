package com.hzj.alipay.provider.alipay.oauth2.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.alipay.properties.AlipayProperties;
import com.hzj.alipay.provider.alipay.oauth2.AlipayOAuth2ConfigProvider;
import com.hzj.alipay.provider.alipay.oauth2.entity.AlipayOAuth2Config;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的支付宝 OAuth2 配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAlipayOAuth2ConfigProvider implements AlipayOAuth2ConfigProvider {

    private final AlipayProperties properties;

    @Override
    public AlipayOAuth2Config getConfig() {
        return BeanUtil.copyProperties(properties, AlipayOAuth2Config.class);
    }
}
