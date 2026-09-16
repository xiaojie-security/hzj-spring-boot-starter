package com.hzj.aliyun.config;

import com.hzj.aliyun.core.imm.AliyunImmService;
import com.hzj.aliyun.core.imm.impl.DefaultAliyunImmService;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import com.hzj.aliyun.provider.aliyun.imm.AliyunImmConfigProvider;
import com.hzj.aliyun.provider.aliyun.imm.entity.AliyunImmConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云 IMM 配置。
 */
@AutoConfiguration
@ConditionalOnBean(AliyunImmConfigProvider.class)
public class AliyunImmConfiguration extends AliyunBaseConfiguration {

    @Bean
    @ConditionalOnMissingBean(com.aliyun.imm20200930.Client.class)
    public com.aliyun.imm20200930.Client immClient(AliyunCredentialRegistry credentialRegistry,
                                                    AliyunImmConfigProvider configProvider) throws Exception {
        if (configProvider.getConfig() == null) {
            return null;
        }

        AliyunImmConfig imm = configProvider.getConfig();

        com.aliyun.teaopenapi.models.Config config = credentialRegistry.createOpenApiConfig(imm);
        config.setEndpoint(imm.getEndpointOverride());
        return new com.aliyun.imm20200930.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunImmService.class)
    public AliyunImmService aliyunImmService(AliyunImmConfigProvider configProvider,
                                             com.aliyun.imm20200930.Client immClient) {
        return new DefaultAliyunImmService(configProvider, immClient);
    }
}
