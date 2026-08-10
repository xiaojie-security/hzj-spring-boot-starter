package com.aliyun.provider.aliyun.imm.impl;

import cn.hutool.core.bean.BeanUtil;
import com.aliyun.properties.AliyunImmProperties;
import com.aliyun.provider.aliyun.imm.AliyunImmConfigProvider;
import com.aliyun.provider.aliyun.imm.entity.AliyunImmConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的 IMM 配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAliyunImmConfigProvider implements AliyunImmConfigProvider {

    private final AliyunImmProperties properties;

    @Override
    public AliyunImmConfig getConfig() {
        return BeanUtil.copyProperties(properties, AliyunImmConfig.class);
    }
}
