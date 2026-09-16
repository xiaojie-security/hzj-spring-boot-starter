package com.hzj.aliyun.config;

import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云基础配置入口。
 * 仅负责注册凭证注册器，具体客户端装配由各能力配置类负责。
 */
@AutoConfiguration
public class AliyunConfiguration {

    /**
     * 注册统一凭证注册器。
     *
     * @return 凭证注册器
     */
    @Bean
    @ConditionalOnMissingBean(AliyunCredentialRegistry.class)
    public AliyunCredentialRegistry aliyunCredentialRegistry() {
        return new AliyunCredentialRegistry();
    }

}
