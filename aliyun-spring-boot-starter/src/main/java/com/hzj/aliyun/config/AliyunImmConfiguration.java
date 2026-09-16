package com.hzj.aliyun.config;

import com.hzj.aliyun.core.imm.AliyunImmService;
import com.hzj.aliyun.core.imm.impl.DefaultAliyunImmService;
import com.hzj.aliyun.provider.aliyun.common.entity.AliyunCredentialConfig;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import com.hzj.aliyun.provider.aliyun.imm.AliyunImmRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.imm.AliyunImmStaticConfigProvider;
import com.hzj.aliyun.provider.aliyun.imm.entity.AliyunImmStaticConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云 IMM 配置。
 */
@AutoConfiguration
@ConditionalOnBean({AliyunImmStaticConfigProvider.class, AliyunImmRuntimeConfigProvider.class})
public class AliyunImmConfiguration extends AliyunBaseConfiguration {

    @Bean
    @ConditionalOnMissingBean(com.aliyun.imm20200930.Client.class)
    public com.aliyun.imm20200930.Client immClient(AliyunCredentialRegistry credentialRegistry,
                                                    AliyunImmStaticConfigProvider configProvider) throws Exception {
        AliyunImmStaticConfig imm = configProvider.getConfig();
        if (imm == null) {
            throw new IllegalStateException("AliyunImmStaticConfigProvider 返回的配置不能为空");
        }
        AliyunCredentialConfig credentialConfig = imm.snapshotCredentialConfig();

        com.aliyun.teaopenapi.models.Config config = credentialRegistry.createOpenApiConfig(credentialConfig)
                .setEndpoint(imm.getEndpointOverride())
                .setRegionId(imm.getRegion());
        return new com.aliyun.imm20200930.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunImmService.class)
    public AliyunImmService aliyunImmService(AliyunImmRuntimeConfigProvider configProvider,
                                             com.aliyun.imm20200930.Client immClient) {
        return new DefaultAliyunImmService(configProvider, immClient);
    }
}
