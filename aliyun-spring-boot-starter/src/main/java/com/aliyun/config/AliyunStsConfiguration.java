package com.aliyun.config;

import com.aliyun.core.sts.AliyunStsService;
import com.aliyun.properties.AliyunStsProperties;
import com.aliyun.provider.aliyun.global.AliyunGlobalConfigProvider;
import com.aliyun.provider.aliyun.global.entity.AliyunGlobalConfig;
import com.aliyun.provider.aliyun.sts.AliyunStsConfigProvider;
import com.aliyun.provider.aliyun.sts.impl.PropertiesAliyunStsConfigProvider;
import com.aliyun.core.sts.impl.DefaultAliyunStsService;
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
@ConditionalOnProperty(prefix = "aliyun.sts", name = "enable", havingValue = "true", matchIfMissing = true)
public class AliyunStsConfiguration extends AliyunBaseConfiguration  {
    private final AliyunStsConfigProvider configProvider;

    @Bean
    @ConditionalOnMissingBean(AliyunStsConfigProvider.class)
    public AliyunStsConfigProvider aliyunStsConfigProvider(AliyunStsProperties properties) {
        return new PropertiesAliyunStsConfigProvider(properties);
    }


    @Bean
    @ConditionalOnMissingBean(AliyunStsService.class)
    public AliyunStsService aliyunStsService(AliyunGlobalConfigProvider globalConfigProvider) throws Exception {
        AliyunGlobalConfig globalConfig = globalConfigProvider.getConfig();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(globalConfig.getAccessKeyId())
                .setAccessKeySecret(globalConfig.getAccessKeySecret())
                .setEndpoint(configProvider.getConfig().getEndpoint());
        return new DefaultAliyunStsService(configProvider, new com.aliyun.sts20150401.Client(config));
    }

}
