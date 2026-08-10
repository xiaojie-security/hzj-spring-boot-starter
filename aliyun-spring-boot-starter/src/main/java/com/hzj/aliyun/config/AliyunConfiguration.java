package com.hzj.aliyun.config;

import com.hzj.aliyun.core.sts.AliyunStsService;
import com.hzj.aliyun.properties.*;
import com.hzj.aliyun.provider.aliyun.global.AliyunGlobalConfigProvider;
import com.hzj.aliyun.provider.aliyun.global.impl.PropertiesAliyunGlobalConfigProvider;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云基础配置入口。
 * 仅负责启用配置属性绑定，具体客户端装配由各子配置类负责。
 */
@Configuration
@EnableConfigurationProperties({
        AliyunImmProperties.class,
        AliyunOssProperties.class,
        AliyunPnsProperties.class,
        AliyunSmsProperties.class,
        AliyunStsProperties.class,
        AliyunGlobalProperties.class
})
public class AliyunConfiguration {

    /**
     * 注册全局凭证配置提供者。
     *
     * @param properties 全局配置属性
     * @return 全局凭证配置提供者
     */
    @Bean
    @ConditionalOnMissingBean(AliyunGlobalConfigProvider.class)
    public AliyunGlobalConfigProvider aliyunGlobalConfigProvider(AliyunGlobalProperties properties) {
        return new PropertiesAliyunGlobalConfigProvider(properties);
    }

    /**
     * 注册统一凭证注册器。
     *
     * @param provider 全局凭证配置提供者
     * @return 凭证注册器
     */
    @Bean
    @ConditionalOnMissingBean
    public AliyunCredentialRegistry aliyunCredentialRegistry(AliyunGlobalConfigProvider provider,
                                                             ObjectProvider<AliyunStsService> stsServiceProvider) {
        return new AliyunCredentialRegistry(provider, stsServiceProvider);
    }

}
