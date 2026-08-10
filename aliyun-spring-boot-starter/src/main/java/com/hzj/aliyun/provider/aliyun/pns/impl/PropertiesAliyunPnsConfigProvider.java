package com.hzj.aliyun.provider.aliyun.pns.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.aliyun.properties.AliyunPnsProperties;
import com.hzj.aliyun.provider.aliyun.pns.AliyunPnsConfigProvider;
import com.hzj.aliyun.provider.aliyun.pns.entity.AliyunPnsConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的号码认证服务配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAliyunPnsConfigProvider implements AliyunPnsConfigProvider {

    private final AliyunPnsProperties properties;

    @Override
    public AliyunPnsConfig getConfig() {
        return BeanUtil.copyProperties(properties, AliyunPnsConfig.class);
    }
}
