package com.aliyun.config;

import com.aliyun.core.imm.AliyunImmService;
import com.aliyun.core.imm.impl.DefaultAliyunImmService;
import com.aliyun.properties.AliyunImmProperties;
import com.aliyun.utils.AliyunCredentialRegistry;
import com.aliyun.provider.aliyun.imm.AliyunImmConfigProvider;
import com.aliyun.provider.aliyun.imm.entity.AliyunImmConfig;
import com.aliyun.provider.aliyun.imm.impl.PropertiesAliyunImmConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云 IMM 配置。
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "aliyun.imm", name = "enable", havingValue = "true", matchIfMissing = true)
public class AliyunImmConfiguration extends AliyunBaseConfiguration {

    private final AliyunImmConfigProvider configProvider;

    @Bean
    @ConditionalOnMissingBean(AliyunImmConfigProvider.class)
    public AliyunImmConfigProvider aliyunImmConfigProvider(AliyunImmProperties properties) {
        return new PropertiesAliyunImmConfigProvider(properties);
    }


    @Bean
    @ConditionalOnMissingBean(com.aliyun.imm20200930.Client.class)
    public com.aliyun.imm20200930.Client immClient(AliyunCredentialRegistry credentialRegistry) throws Exception {
        if (configProvider.getConfig() == null) {
            return null;
        }

        AliyunImmConfig imm = configProvider.getConfig();

        com.aliyun.teaopenapi.models.Config config = credentialRegistry.createOpenApiConfig(
                imm.getRamRoleArn(), 3600L);
        config.setEndpoint(imm.getEndpointOverride());
        return new com.aliyun.imm20200930.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunImmService.class)
    public AliyunImmService aliyunImmService(com.aliyun.imm20200930.Client immClient) {
        return new DefaultAliyunImmService(configProvider, immClient);
    }
}
