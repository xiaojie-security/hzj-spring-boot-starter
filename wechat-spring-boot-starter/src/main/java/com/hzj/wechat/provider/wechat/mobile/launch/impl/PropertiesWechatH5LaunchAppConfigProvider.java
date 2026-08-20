package com.hzj.wechat.provider.wechat.mobile.launch.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.wechat.properties.WechatH5LaunchAppProperties;
import com.hzj.wechat.provider.wechat.mobile.launch.WechatH5LaunchAppConfigProvider;
import com.hzj.wechat.provider.wechat.mobile.launch.entity.WechatH5LaunchAppConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的微信 H5 拉起 App 配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesWechatH5LaunchAppConfigProvider implements WechatH5LaunchAppConfigProvider {

    /** 微信 H5 拉起 App 配置属性。 */
    private final WechatH5LaunchAppProperties properties;

    /**
     * 获取微信 H5 拉起 App 配置。
     *
     * @return 微信 H5 拉起 App 配置
     */
    @Override
    public WechatH5LaunchAppConfig getConfig() {
        return BeanUtil.copyProperties(properties, WechatH5LaunchAppConfig.class);
    }
}
