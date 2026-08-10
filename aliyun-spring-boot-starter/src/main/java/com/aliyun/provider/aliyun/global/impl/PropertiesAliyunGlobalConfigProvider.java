package com.aliyun.provider.aliyun.global.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aliyun.properties.AliyunGlobalProperties;
import com.aliyun.provider.aliyun.global.AliyunGlobalConfigProvider;
import com.aliyun.provider.aliyun.global.entity.AliyunGlobalConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的全局凭证配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAliyunGlobalConfigProvider implements AliyunGlobalConfigProvider {

    private final AliyunGlobalProperties properties;

    @Override
    public AliyunGlobalConfig getConfig() {
        return BeanUtil.copyProperties(properties, AliyunGlobalConfig.class);
    }
}
