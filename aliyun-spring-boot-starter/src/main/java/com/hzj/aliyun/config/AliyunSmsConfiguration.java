package com.hzj.aliyun.config;

import com.hzj.aliyun.core.sms.AliyunSmsService;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import com.hzj.aliyun.provider.aliyun.common.entity.AliyunCredentialConfig;
import com.hzj.aliyun.provider.aliyun.sms.AliyunSmsRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.sms.AliyunSmsStaticConfigProvider;
import com.hzj.aliyun.provider.aliyun.sms.entity.AliyunSmsStaticConfig;
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
@ConditionalOnBean({AliyunSmsStaticConfigProvider.class, AliyunSmsRuntimeConfigProvider.class})
public class AliyunSmsConfiguration extends AliyunBaseConfiguration {

    @Bean("aliyunSmsClient")
    @ConditionalOnMissingBean(com.aliyun.dysmsapi20170525.Client.class)
    public com.aliyun.dysmsapi20170525.Client client(AliyunCredentialRegistry credentialRegistry,
                                                     AliyunSmsStaticConfigProvider configProvider) throws Exception{
        AliyunSmsStaticConfig sms = configProvider.getConfig();
        if (sms == null) {
            throw new IllegalStateException("AliyunSmsStaticConfigProvider 返回的配置不能为空");
        }
        AliyunCredentialConfig credentialConfig = sms.snapshotCredentialConfig();
        Config config = credentialRegistry.createOpenApiConfig(credentialConfig)
                .setEndpoint(sms.getEndpoint())
                .setRegionId(sms.getRegion());
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    @Bean
    @ConditionalOnMissingBean(AliyunSmsService.class)
    public AliyunSmsService aliyunSmsService(AliyunSmsRuntimeConfigProvider configProvider,
                                             com.aliyun.dysmsapi20170525.Client aliyunSmsClient) {
        return new DefaultAliyunSmsService(configProvider, aliyunSmsClient);
    }
}
