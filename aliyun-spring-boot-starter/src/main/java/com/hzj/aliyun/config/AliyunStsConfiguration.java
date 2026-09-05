package com.hzj.aliyun.config;

import com.hzj.aliyun.core.sts.AliyunStsService;
import com.hzj.aliyun.provider.aliyun.global.AliyunGlobalConfigProvider;
import com.hzj.aliyun.provider.aliyun.global.entity.AliyunGlobalConfig;
import com.hzj.aliyun.provider.aliyun.sts.AliyunStsConfigProvider;
import com.hzj.aliyun.core.sts.impl.DefaultAliyunStsService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云号码认证配置。
 */
@AutoConfiguration
@ConditionalOnBean({AliyunStsConfigProvider.class, AliyunGlobalConfigProvider.class})
public class AliyunStsConfiguration extends AliyunBaseConfiguration  {

    @Bean
    @ConditionalOnMissingBean(AliyunStsService.class)
    public AliyunStsService aliyunStsService(AliyunGlobalConfigProvider globalConfigProvider,
                                              AliyunStsConfigProvider configProvider) throws Exception {
        AliyunGlobalConfig globalConfig = globalConfigProvider.getConfig();
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(globalConfig.getAccessKeyId())
                .setAccessKeySecret(globalConfig.getAccessKeySecret())
                .setEndpoint(configProvider.getConfig().getEndpoint());
        return new DefaultAliyunStsService(configProvider, new com.aliyun.sts20150401.Client(config));
    }

}
