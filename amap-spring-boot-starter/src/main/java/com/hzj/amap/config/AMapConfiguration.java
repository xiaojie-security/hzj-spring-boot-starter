package com.hzj.amap.config;

import com.hzj.amap.core.webapi.AMapWebApiService;
import com.hzj.amap.core.webapi.impl.DefaultAMapWebApiService;
import com.hzj.amap.provider.webapi.AMapWebApiStaticConfigProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 高德地图自动配置。
 * <p>
 * 仅当使用方提供了 {@link AMapWebApiStaticConfigProvider} Bean 时才会装配。
 */
@AutoConfiguration
@ConditionalOnBean(AMapWebApiStaticConfigProvider.class)
public class AMapConfiguration {

    /**
     * 注册高德 Web 服务 API 服务。
     *
     * @param provider 高德 Web 服务动态配置提供者
     * @return 高德 Web 服务 API 服务
     */
    @Bean
    @ConditionalOnMissingBean(AMapWebApiService.class)
    public AMapWebApiService amapWebApiService(AMapWebApiStaticConfigProvider provider,
                                               ObjectProvider<ObjectMapper> objectMapperProvider) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable();
        return objectMapper == null
                ? new DefaultAMapWebApiService(provider)
                : new DefaultAMapWebApiService(provider, new okhttp3.OkHttpClient.Builder().build(), objectMapper);
    }
}
