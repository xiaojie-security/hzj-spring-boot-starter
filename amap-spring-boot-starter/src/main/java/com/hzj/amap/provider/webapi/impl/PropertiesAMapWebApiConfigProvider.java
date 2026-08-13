package com.hzj.amap.provider.webapi.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hzj.amap.properties.AMapWebApiProperties;
import com.hzj.amap.provider.webapi.AMapWebApiConfigProvider;
import com.hzj.amap.provider.webapi.entity.WebApiConfig;
import lombok.RequiredArgsConstructor;

/**
 * 基于 Spring 配置属性的高德 Web 服务 API 配置提供者。
 */
@RequiredArgsConstructor
public class PropertiesAMapWebApiConfigProvider implements AMapWebApiConfigProvider {

    /**
     * 高德 Web 服务 API 配置属性。
     */
    private final AMapWebApiProperties properties;

    /**
     * 获取高德 Web 服务 API 配置。
     *
     * @return 高德 Web 服务 API 配置
     */
    @Override
    public WebApiConfig getConfig() {
        return BeanUtil.copyProperties(properties, WebApiConfig.class);
    }
}
