package com.aliyun.utils;

import com.aliyun.core.sts.AliyunStsService;
import com.aliyun.core.sts.domain.AliyunStsSecurityCredential;
import com.aliyun.provider.aliyun.global.AliyunGlobalConfigProvider;
import com.aliyun.provider.aliyun.global.entity.AliyunGlobalConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;

/**
 * 阿里云客户端凭证注册器。
 *
 * <p>STS 模式通过 {@link AliyunStsService} 获取临时凭证，AK 模式直接使用全局固定凭证。</p>
 */
@RequiredArgsConstructor
public class AliyunCredentialRegistry {

    private final AliyunGlobalConfigProvider configProvider;
    private final ObjectProvider<AliyunStsService> stsServiceProvider;

    /**
     * 创建 OpenAPI 客户端配置。
     *
     * @return OpenAPI 配置
     * @throws Exception 创建 STS 凭证客户端异常
     */
    public com.aliyun.teaopenapi.models.Config createOpenApiConfig(String ramRoleArn, Long expire) throws Exception {
        AliyunGlobalConfig config = getConfig();
        com.aliyun.teaopenapi.models.Config openApiConfig = new com.aliyun.teaopenapi.models.Config();
        if (config.useSts()) {
            AliyunStsSecurityCredential credential = getStsCredential(ramRoleArn);
            openApiConfig.setAccessKeyId(credential.getAccessKeyId());
            openApiConfig.setAccessKeySecret(credential.getAccessKeySecret());
            openApiConfig.setSecurityToken(credential.getSecurityToken());
        } else {
            openApiConfig.setAccessKeyId(config.getAccessKeyId());
            openApiConfig.setAccessKeySecret(config.getAccessKeySecret());
        }
        return openApiConfig;
    }

    /**
     * 创建 STS 服务自身使用的 AK 配置。
     *
     * <p>STS 客户端负责用全局 AK 扮演 RAM 角色，因此不能使用已经通过 STS 获取的凭证。</p>
     *
     * @return STS OpenAPI 配置
     */
    public com.aliyun.teaopenapi.models.Config createStsClientConfig() {
        AliyunGlobalConfig config = getConfig();
        return new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(config.getAccessKeyId())
                .setAccessKeySecret(config.getAccessKeySecret());
    }

    /**
     * 获取 OSS V2 使用的凭证。
     *
     * @return OSS V2 凭证
     */
    public com.aliyun.sdk.service.oss2.credentials.Credentials getOssV2Credentials(String ramRoleArn, Long expire) {
        AliyunGlobalConfig config = getConfig();
        if (!config.useSts()) {
            return new com.aliyun.sdk.service.oss2.credentials.Credentials(
                    config.getAccessKeyId(), config.getAccessKeySecret(), null);
        }
        try {
            AliyunStsSecurityCredential credential = getStsCredential(ramRoleArn);
            return new com.aliyun.sdk.service.oss2.credentials.Credentials(
                    credential.getAccessKeyId(), credential.getAccessKeySecret(), credential.getSecurityToken());
        } catch (Exception e) {
            throw new IllegalStateException("获取 OSS STS 凭证失败", e);
        }
    }

    /**
     * 获取旧版 OSS 使用的凭证。
     *
     * @return OSS 凭证
     */
    public com.aliyun.oss.common.auth.Credentials getOssCredentials(String ramRoleArn, Long expire) {
        AliyunGlobalConfig config = getConfig();
        if (!config.useSts()) {
            return new com.aliyun.oss.common.auth.DefaultCredentials(
                    config.getAccessKeyId(), config.getAccessKeySecret());
        }
        try {
            AliyunStsSecurityCredential credential = getStsCredential(ramRoleArn);
            return new com.aliyun.oss.common.auth.DefaultCredentials(
                    credential.getAccessKeyId(), credential.getAccessKeySecret(), credential.getSecurityToken());
        } catch (Exception e) {
            throw new IllegalStateException("获取 OSS STS 凭证失败", e);
        }
    }

    private AliyunGlobalConfig getConfig() {
        AliyunGlobalConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("AliyunGlobalConfigProvider 返回的配置不能为空");
        }
        return config;
    }

    private AliyunStsSecurityCredential getStsCredential(String ramRoleArn) {
        AliyunStsService stsService = stsServiceProvider.getIfAvailable();
        if (stsService == null) {
            throw new IllegalStateException("STS 模式需要启用 AliyunStsService");
        }
        AliyunStsSecurityCredential credential = stsService.generateStsSecurityCredential(ramRoleArn);
        if (credential == null) {
            throw new IllegalStateException("AliyunStsService 未返回有效的 STS 凭证");
        }
        return credential;
    }
}
