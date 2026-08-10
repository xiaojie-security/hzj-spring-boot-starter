package com.aliyun.provider.aliyun.sts.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aliyun.properties.AliyunStsProperties;
import com.aliyun.provider.aliyun.sts.AliyunStsConfigProvider;
import com.aliyun.provider.aliyun.sts.entity.AliyunStsConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的 STS 配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAliyunStsConfigProvider implements AliyunStsConfigProvider {

    private final AliyunStsProperties properties;

    @Override
    public AliyunStsConfig getConfig() {
        return BeanUtil.copyProperties(properties, AliyunStsConfig.class);
    }
}
