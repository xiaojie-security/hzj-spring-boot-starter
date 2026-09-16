package com.hzj.aliyun.config;

import com.aliyun.cloudauth20190307.Client;
import com.aliyun.teaopenapi.models.Config;
import com.hzj.aliyun.core.faceverification.AliyunFaceVerificationService;
import com.hzj.aliyun.core.faceverification.impl.DefaultAliyunFaceVerificationService;
import com.hzj.aliyun.provider.aliyun.common.entity.AliyunCredentialConfig;
import com.hzj.aliyun.provider.aliyun.faceverification.AliyunFaceVerificationRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.faceverification.AliyunFaceVerificationStaticConfigProvider;
import com.hzj.aliyun.provider.aliyun.faceverification.entity.AliyunFaceVerificationStaticConfig;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云金融级实人认证自动装配配置。
 */
@AutoConfiguration
@ConditionalOnBean({AliyunFaceVerificationStaticConfigProvider.class,
        AliyunFaceVerificationRuntimeConfigProvider.class})
public class AliyunFaceVerificationConfiguration extends AliyunBaseConfiguration {

    /**
     * 创建金融级实人认证客户端。
     *
     * @param credentialRegistry 阿里云能力级凭证注册器
     * @param configProvider 实人认证启动期静态配置提供者
     * @return 实人认证客户端
     * @throws Exception SDK 客户端初始化异常
     */
    @Bean("aliyunFaceVerificationClient")
    @ConditionalOnMissingBean(Client.class)
    public Client aliyunFaceVerificationClient(AliyunCredentialRegistry credentialRegistry,
                                                AliyunFaceVerificationStaticConfigProvider configProvider)
            throws Exception {
        AliyunFaceVerificationStaticConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("AliyunFaceVerificationStaticConfigProvider 返回的配置不能为空");
        }
        AliyunCredentialConfig credentialConfig = config.snapshotCredentialConfig();
        Config clientConfig = credentialRegistry.createOpenApiConfig(credentialConfig)
                .setEndpoint(config.getEndpoint())
                .setRegionId(config.getRegion());
        return new Client(clientConfig);
    }

    /**
     * 注册金融级实人认证服务。
     *
     * @param configProvider 实人认证运行时配置提供者
     * @param client 实人认证 SDK 客户端
     * @return 实人认证服务
     */
    @Bean
    @ConditionalOnMissingBean(AliyunFaceVerificationService.class)
    public AliyunFaceVerificationService aliyunFaceVerificationService(
            AliyunFaceVerificationRuntimeConfigProvider configProvider,
            Client client) {
        return new DefaultAliyunFaceVerificationService(configProvider, client);
    }
}
