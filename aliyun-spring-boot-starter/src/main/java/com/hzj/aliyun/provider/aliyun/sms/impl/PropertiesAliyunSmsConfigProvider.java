package com.hzj.aliyun.provider.aliyun.sms.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.aliyun.properties.AliyunSmsProperties;
import com.hzj.aliyun.provider.aliyun.sms.AliyunSmsConfigProvider;
import com.hzj.aliyun.provider.aliyun.sms.entity.AliyunSmsConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的短信配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAliyunSmsConfigProvider implements AliyunSmsConfigProvider {

    private final AliyunSmsProperties properties;

    @Override
    public AliyunSmsConfig getConfig() {
        return BeanUtil.copyProperties(properties, AliyunSmsConfig.class);
    }
}
