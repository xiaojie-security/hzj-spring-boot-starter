package com.aliyun.config;

import com.aliyun.core.oss.AliyunOssService;
import com.aliyun.core.oss.impl.DefaultAliyunOssService;
import com.aliyun.utils.AliyunCredentialRegistry;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.properties.AliyunOssProperties;
import com.aliyun.provider.aliyun.oss.AliyunOssConfigProvider;
import com.aliyun.provider.aliyun.oss.entity.AliyunOssConfig;
import com.aliyun.provider.aliyun.oss.impl.PropertiesAliyunOssConfigProvider;
import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 阿里云 OSS 配置。
 */
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enable", havingValue = "true", matchIfMissing = true)
public class AliyunOssConfiguration extends AliyunBaseConfiguration {

    private final AliyunOssConfigProvider configProvider;

    @Bean
    @ConditionalOnMissingBean(AliyunOssConfigProvider.class)
    public AliyunOssConfigProvider aliyunOssConfigProvider(AliyunOssProperties properties) {
        return new PropertiesAliyunOssConfigProvider(properties);
    }


    @Bean
    @ConditionalOnMissingBean(OSSClient.class)
    public OSSClient ossV2Client(AliyunCredentialRegistry credentialRegistry) throws Exception {
        AliyunOssConfig oss = configProvider.getConfig();

        CredentialsProvider credentialsProviderV2 = new CredentialsProviderSupplier(() -> {
            try {
                AliyunOssConfig current = configProvider.getConfig();
                return credentialRegistry.getOssV2Credentials(current.getRamRoleArn(), current.getExpire());
            } catch (Exception e) {
                throw new RuntimeException("获取凭证失败", e);
            }
        });

        return OSSClient.newBuilder()
                .credentialsProvider(credentialsProviderV2)
                .region(oss.getRegion())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(com.aliyun.oss.OSS.class)
    public com.aliyun.oss.OSS ossClient(AliyunCredentialRegistry credentialRegistry) throws Exception {
        AliyunOssConfig oss = configProvider.getConfig();
        com.aliyun.oss.common.auth.CredentialsProvider credentialsProvider = new com.aliyun.oss.common.auth.CredentialsProvider() {
            @Override
            public void setCredentials(com.aliyun.oss.common.auth.Credentials credentials) {
            }

            @Override
            public com.aliyun.oss.common.auth.Credentials getCredentials() {
                AliyunOssConfig current = configProvider.getConfig();
                return credentialRegistry.getOssCredentials(current.getRamRoleArn(), current.getExpire());
            }
        };

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        return OSSClientBuilder.create()
                .endpoint(oss.getEndpoint())
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(oss.getRegion())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(AliyunOssService.class)
    public AliyunOssService aliyunOssService(OSSClient ossV2Client, com.aliyun.oss.OSS ossClient) {
        return new DefaultAliyunOssService(ossV2Client, ossClient, configProvider);
    }
}
