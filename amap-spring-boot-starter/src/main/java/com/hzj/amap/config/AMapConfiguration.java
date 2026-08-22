package com.hzj.amap.config;

import com.hzj.amap.core.webapi.AMapWebApiService;
import com.hzj.amap.core.webapi.impl.DefaultAMapWebApiService;
import com.hzj.amap.properties.AMapWebApiProperties;
import com.hzj.amap.provider.webapi.AMapWebApiConfigProvider;
import com.hzj.amap.provider.webapi.impl.PropertiesAMapWebApiConfigProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 高德地图自动配置。
 */
@AutoConfiguration
@EnableConfigurationProperties(AMapWebApiProperties.class)
public class AMapConfiguration {

    /**
     * 注册基于配置属性的高德 Web 服务 API 配置提供者。
     *
     * @param properties 高德 Web 服务 API 配置属性
     * @return 高德 Web 服务 API 配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(AMapWebApiConfigProvider.class)
    public AMapWebApiConfigProvider amapWebApiConfigProvider(AMapWebApiProperties properties) {
        return new PropertiesAMapWebApiConfigProvider(properties);
    }

    /**
     * 注册高德 Web 服务 API 服务。
     *
     * @param provider 高德 Web 服务动态配置提供者
     * @return 高德 Web 服务 API 服务
     */
    @Bean
    @ConditionalOnMissingBean(AMapWebApiService.class)
    public AMapWebApiService amapWebApiService(AMapWebApiConfigProvider provider,
                                               ObjectProvider<ObjectMapper> objectMapperProvider) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable();
        return objectMapper == null
                ? new DefaultAMapWebApiService(provider)
                : new DefaultAMapWebApiService(provider, new okhttp3.OkHttpClient.Builder().build(), objectMapper);
    }
}
