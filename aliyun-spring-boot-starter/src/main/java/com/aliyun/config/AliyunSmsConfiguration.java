package com.aliyun.config;

import com.aliyun.core.sms.AliyunSmsService;
import com.aliyun.properties.AliyunSmsProperties;
import com.aliyun.utils.AliyunCredentialRegistry;
import com.aliyun.provider.aliyun.sms.AliyunSmsConfigProvider;
import com.aliyun.provider.aliyun.sms.entity.AliyunSmsConfig;
import com.aliyun.provider.aliyun.sms.impl.PropertiesAliyunSmsConfigProvider;
import com.aliyun.core.sms.impl.DefaultAliyunSmsService;
import com.aliyun.teaopenapi.models.Config;
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
@ConditionalOnProperty(prefix = "aliyun.sms", name = "enable", havingValue = "true", matchIfMissing = true)
public class AliyunSmsConfiguration extends AliyunBaseConfiguration {

    private final AliyunSmsConfigProvider configProvider;

    @Bean
    @ConditionalOnMissingBean(AliyunSmsConfigProvider.class)
    public AliyunSmsConfigProvider aliyunSmsConfigProvider(AliyunSmsProperties properties) {
        return new PropertiesAliyunSmsConfigProvider(properties);
    }

    @Bean("aliyunSmsClient")
    @ConditionalOnMissingBean(com.aliyun.dysmsapi20170525.Client.class)
    public com.aliyun.dysmsapi20170525.Client client(AliyunCredentialRegistry credentialRegistry) throws Exception{
        AliyunSmsConfig sms = configProvider.getConfig();
        Config config = credentialRegistry.createOpenApiConfig(sms.getRamRoleArn(), 3600L);
        config.endpoint = sms.getEndpoint();
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunSmsService.class)
    public AliyunSmsService aliyunSmsService(com.aliyun.dysmsapi20170525.Client aliyunSmsClient) {
        return new DefaultAliyunSmsService(configProvider, aliyunSmsClient);
    }
}
