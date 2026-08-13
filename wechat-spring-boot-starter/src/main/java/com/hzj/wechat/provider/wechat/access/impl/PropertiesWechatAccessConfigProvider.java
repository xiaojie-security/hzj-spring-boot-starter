package com.hzj.wechat.provider.wechat.access.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.wechat.properties.WechatAccessProperties;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import com.hzj.wechat.provider.wechat.access.entity.WechatAccessConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的微信接口调用凭据配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesWechatAccessConfigProvider implements WechatAccessConfigProvider {

    /** 微信接口调用凭据配置属性。 */
    private final WechatAccessProperties properties;

    /**
     * 获取微信接口调用凭据配置。
     *
     * @return 微信接口调用凭据配置
     */
    @Override
    public WechatAccessConfig getConfig() {
        return BeanUtil.copyProperties(properties, WechatAccessConfig.class);
    }
}
