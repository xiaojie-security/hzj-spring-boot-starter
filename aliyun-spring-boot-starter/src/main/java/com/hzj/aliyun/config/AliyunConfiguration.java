package com.hzj.aliyun.config;

import com.hzj.aliyun.core.sts.AliyunStsService;
import com.hzj.aliyun.provider.aliyun.global.AliyunGlobalConfigProvider;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云基础配置入口。
 * 仅负责在存在全局配置提供者时注册凭证注册器，具体客户端装配由各子配置类负责。
 */
@Configuration
public class AliyunConfiguration {

    /**
     * 注册统一凭证注册器。
     *
     * @param provider 全局凭证配置提供者
     * @return 凭证注册器
     */
    @Bean
    @ConditionalOnBean(AliyunGlobalConfigProvider.class)
    @ConditionalOnMissingBean
    public AliyunCredentialRegistry aliyunCredentialRegistry(AliyunGlobalConfigProvider provider,
                                                             ObjectProvider<AliyunStsService> stsServiceProvider) {
        return new AliyunCredentialRegistry(provider, stsServiceProvider);
    }

}
