package com.hzj.wechat.provider.wechat.qrcode.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.wechat.properties.WechatQrCodeProperties;
import com.hzj.wechat.provider.wechat.qrcode.WechatQrCodeConfigProvider;
import com.hzj.wechat.provider.wechat.qrcode.entity.WechatQrCodeConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的微信小程序二维码配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesWechatQrCodeConfigProvider implements WechatQrCodeConfigProvider {

    /** 微信小程序二维码配置属性。 */
    private final WechatQrCodeProperties properties;

    /**
     * 获取微信小程序二维码配置。
     *
     * @return 微信小程序二维码配置
     */
    @Override
    public WechatQrCodeConfig getConfig() {
        return BeanUtil.copyProperties(properties, WechatQrCodeConfig.class);
    }
}
