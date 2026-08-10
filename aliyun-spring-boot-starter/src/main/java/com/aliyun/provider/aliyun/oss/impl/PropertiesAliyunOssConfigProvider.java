package com.aliyun.provider.aliyun.oss.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aliyun.properties.AliyunOssProperties;
import com.aliyun.provider.aliyun.oss.AliyunOssConfigProvider;
import com.aliyun.provider.aliyun.oss.entity.AliyunOssConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的 OSS 配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAliyunOssConfigProvider implements AliyunOssConfigProvider {

    private final AliyunOssProperties properties;

    @Override
    public AliyunOssConfig getConfig() {
        return BeanUtil.copyProperties(properties, AliyunOssConfig.class);
    }
}
