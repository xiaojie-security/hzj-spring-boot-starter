package com.hzj.aliyun.config;

import com.hzj.aliyun.core.pns.AliyunPnsService;
import com.hzj.aliyun.core.pns.impl.DefaultAliyunPnsService;
import com.hzj.aliyun.properties.AliyunPnsProperties;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import com.hzj.aliyun.provider.aliyun.pns.AliyunPnsConfigProvider;
import com.hzj.aliyun.provider.aliyun.pns.entity.AliyunPnsConfig;
import com.hzj.aliyun.provider.aliyun.pns.impl.PropertiesAliyunPnsConfigProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云号码认证配置。
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "aliyun.pns", name = "enable", havingValue = "true", matchIfMissing = true)
public class AliyunPnsConfiguration extends AliyunBaseConfiguration {

    private final AliyunPnsConfigProvider configProvider;

    @Bean
    @ConditionalOnMissingBean(AliyunPnsConfigProvider.class)
    public AliyunPnsConfigProvider aliyunPnsConfigProvider(AliyunPnsProperties properties) {
        return new PropertiesAliyunPnsConfigProvider(properties);
    }


    @Bean("aliyunPnsClient")
    @ConditionalOnMissingBean(com.aliyun.dypnsapi20170525.Client.class)
    public com.aliyun.dypnsapi20170525.Client client(AliyunCredentialRegistry credentialRegistry) throws Exception {
        AliyunPnsConfig pns = configProvider.getConfig();
        com.aliyun.teaopenapi.models.Config config = credentialRegistry.createOpenApiConfig(
                        pns.getRamRoleArn(), 3600L)
                .setEndpoint(pns.getEndpoint())
                .setRegionId(pns.getRegion());

        return new com.aliyun.dypnsapi20170525.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunPnsService.class)
    public AliyunPnsService aliyunPnsService(com.aliyun.dypnsapi20170525.Client aliyunPnsClient) {
        return new DefaultAliyunPnsService(configProvider, aliyunPnsClient);
    }
}
