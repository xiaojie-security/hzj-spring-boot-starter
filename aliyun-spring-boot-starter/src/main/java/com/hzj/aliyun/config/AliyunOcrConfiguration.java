package com.hzj.aliyun.config;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.teaopenapi.models.Config;
import com.hzj.aliyun.core.ocr.AliyunOcrService;
import com.hzj.aliyun.core.ocr.impl.DefaultAliyunOcrService;
import com.hzj.aliyun.provider.aliyun.common.entity.AliyunCredentialConfig;
import com.hzj.aliyun.provider.aliyun.ocr.AliyunOcrRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.ocr.AliyunOcrStaticConfigProvider;
import com.hzj.aliyun.provider.aliyun.ocr.entity.AliyunOcrStaticConfig;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云 OCR 自动装配配置。
 */
@AutoConfiguration
@ConditionalOnBean({AliyunOcrStaticConfigProvider.class, AliyunOcrRuntimeConfigProvider.class})
public class AliyunOcrConfiguration extends AliyunBaseConfiguration {

    /**
     * 创建 OCR 客户端。
     *
     * @param credentialRegistry 阿里云能力级凭证注册器
     * @param configProvider OCR 启动期静态配置提供者
     * @return OCR 客户端
     * @throws Exception SDK 客户端初始化异常
     */
    @Bean("aliyunOcrClient")
    @ConditionalOnMissingBean(Client.class)
    public Client aliyunOcrClient(AliyunCredentialRegistry credentialRegistry,
                                  AliyunOcrStaticConfigProvider configProvider) throws Exception {
        AliyunOcrStaticConfig ocr = configProvider.getConfig();
        if (ocr == null) {
            throw new IllegalStateException("AliyunOcrStaticConfigProvider 返回的配置不能为空");
        }
        AliyunCredentialConfig credentialConfig = ocr.snapshotCredentialConfig();
        Config config = credentialRegistry.createOpenApiConfig(credentialConfig)
                .setEndpoint(ocr.getEndpoint())
                .setRegionId(ocr.getRegion());
        return new Client(config);
    }

    /**
     * 注册 OCR 服务。
     *
     * @param configProvider OCR 运行时配置提供者
     * @param aliyunOcrClient OCR SDK 客户端
     * @return OCR 服务
     */
    @Bean
    @ConditionalOnMissingBean(AliyunOcrService.class)
    public AliyunOcrService aliyunOcrService(AliyunOcrRuntimeConfigProvider configProvider,
                                             Client aliyunOcrClient) {
        return new DefaultAliyunOcrService(configProvider, aliyunOcrClient);
    }
}
