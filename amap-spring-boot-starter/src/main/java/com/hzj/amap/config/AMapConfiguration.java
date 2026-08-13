package com.hzj.amap.config;

import com.hzj.amap.core.webapi.AMapWebApiService;
import com.hzj.amap.core.webapi.impl.DefaultAMapWebApiService;
import com.hzj.amap.provider.webapi.AMapWebApiConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class AMapConfiguration {

    /**
     * 注册高德 Web 服务 API 服务。
     *
     * @param provider 高德 Web 服务动态配置提供者
     * @return 高德 Web 服务 API 服务
     */
    @Bean
    @ConditionalOnBean(AMapWebApiConfigProvider.class)
    @ConditionalOnMissingBean(AMapWebApiService.class)
    public AMapWebApiService amapWebApiService(AMapWebApiConfigProvider provider) {
        return new DefaultAMapWebApiService(provider);
    }
}
