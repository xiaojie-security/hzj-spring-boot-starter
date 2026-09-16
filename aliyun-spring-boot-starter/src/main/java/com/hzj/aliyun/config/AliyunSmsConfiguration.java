package com.hzj.aliyun.config;

import com.hzj.aliyun.core.sms.AliyunSmsService;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import com.hzj.aliyun.provider.aliyun.sms.AliyunSmsConfigProvider;
import com.hzj.aliyun.provider.aliyun.sms.entity.AliyunSmsConfig;
import com.hzj.aliyun.core.sms.impl.DefaultAliyunSmsService;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云号码认证配置。
 */
@AutoConfiguration
@ConditionalOnBean(AliyunSmsConfigProvider.class)
public class AliyunSmsConfiguration extends AliyunBaseConfiguration {

    @Bean("aliyunSmsClient")
    @ConditionalOnMissingBean(com.aliyun.dysmsapi20170525.Client.class)
    public com.aliyun.dysmsapi20170525.Client client(AliyunCredentialRegistry credentialRegistry,
                                                     AliyunSmsConfigProvider configProvider) throws Exception{
        AliyunSmsConfig sms = configProvider.getConfig();
        Config config = credentialRegistry.createOpenApiConfig(sms);
        config.endpoint = sms.getEndpoint();
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunSmsService.class)
    public AliyunSmsService aliyunSmsService(AliyunSmsConfigProvider configProvider,
                                             com.aliyun.dysmsapi20170525.Client aliyunSmsClient) {
        return new DefaultAliyunSmsService(configProvider, aliyunSmsClient);
    }
}
