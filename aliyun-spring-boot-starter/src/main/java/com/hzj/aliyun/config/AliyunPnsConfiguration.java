package com.hzj.aliyun.config;

import com.hzj.aliyun.core.pns.AliyunPnsService;
import com.hzj.aliyun.core.pns.impl.DefaultAliyunPnsService;
import com.hzj.aliyun.provider.aliyun.common.entity.AliyunCredentialConfig;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import com.hzj.aliyun.provider.aliyun.pns.AliyunPnsConfigProvider;
import com.hzj.aliyun.provider.aliyun.pns.entity.AliyunPnsConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云号码认证配置。
 */
@AutoConfiguration
@ConditionalOnBean(AliyunPnsConfigProvider.class)
public class AliyunPnsConfiguration extends AliyunBaseConfiguration {

    @Bean("aliyunPnsClient")
    @ConditionalOnMissingBean(com.aliyun.dypnsapi20170525.Client.class)
    public com.aliyun.dypnsapi20170525.Client client(AliyunCredentialRegistry credentialRegistry,
                                                     AliyunPnsConfigProvider configProvider) throws Exception {
        AliyunPnsConfig pns = configProvider.getConfig();
        if (pns == null) {
            throw new IllegalStateException("AliyunPnsConfigProvider 返回的配置不能为空");
        }
        AliyunCredentialConfig credentialConfig = pns.snapshotCredentialConfig();
        com.aliyun.teaopenapi.models.Config config = credentialRegistry.createOpenApiConfig(credentialConfig)
                .setEndpoint(pns.getEndpoint())
                .setRegionId(pns.getRegion());

        return new com.aliyun.dypnsapi20170525.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunPnsService.class)
    public AliyunPnsService aliyunPnsService(AliyunPnsConfigProvider configProvider,
                                             com.aliyun.dypnsapi20170525.Client aliyunPnsClient) {
        return new DefaultAliyunPnsService(configProvider, aliyunPnsClient);
    }
}
