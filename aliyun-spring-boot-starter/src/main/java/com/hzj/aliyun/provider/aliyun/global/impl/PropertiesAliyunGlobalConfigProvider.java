package com.hzj.aliyun.provider.aliyun.global.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.aliyun.properties.AliyunGlobalProperties;
import com.hzj.aliyun.provider.aliyun.global.AliyunGlobalConfigProvider;
import com.hzj.aliyun.provider.aliyun.global.entity.AliyunGlobalConfig;
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
