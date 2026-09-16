package com.hzj.aliyun.utils;

import com.hzj.aliyun.provider.aliyun.common.entity.AliyunBaseConfig;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyun.tea.TeaException;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 阿里云客户端凭证注册器。
 *
 * <p>凭证解析完全基于当前阿里云能力的配置，不依赖全局账号或全局 STS 服务。</p>
 */
@Slf4j
public class AliyunCredentialRegistry {

    private final ConcurrentMap<StsClientKey, com.aliyun.sts20150401.Client> stsClients = new ConcurrentHashMap<>();

    /**
     * 创建 OpenAPI 客户端配置。
     *
     * @param serviceConfig 当前阿里云能力配置
     * @return OpenAPI 配置
     */
    public com.aliyun.teaopenapi.models.Config createOpenApiConfig(AliyunBaseConfig serviceConfig) {
        ResolvedCredentials credentials = resolveCredentials(serviceConfig);
        com.aliyun.teaopenapi.models.Config openApiConfig = new com.aliyun.teaopenapi.models.Config();
        openApiConfig.setAccessKeyId(credentials.accessKeyId());
        openApiConfig.setAccessKeySecret(credentials.accessKeySecret());
        openApiConfig.setSecurityToken(credentials.securityToken());
        return openApiConfig;
    }

    /**
     * 获取 OSS V2 使用的凭证。
     *
     * @param serviceConfig OSS 配置
     * @return OSS V2 凭证
     */
    public com.aliyun.sdk.service.oss2.credentials.Credentials getOssV2Credentials(AliyunBaseConfig serviceConfig) {
        ResolvedCredentials credentials = resolveCredentials(serviceConfig);
        return new com.aliyun.sdk.service.oss2.credentials.Credentials(
                credentials.accessKeyId(), credentials.accessKeySecret(), credentials.securityToken());
    }

    /**
     * 获取旧版 OSS 使用的凭证。
     *
     * @param serviceConfig OSS 配置
     * @return OSS 凭证
     */
    public com.aliyun.oss.common.auth.Credentials getOssCredentials(AliyunBaseConfig serviceConfig) {
        ResolvedCredentials credentials = resolveCredentials(serviceConfig);
        if (credentials.securityToken() == null) {
            return new com.aliyun.oss.common.auth.DefaultCredentials(
                    credentials.accessKeyId(), credentials.accessKeySecret());
        }
        return new com.aliyun.oss.common.auth.DefaultCredentials(
                credentials.accessKeyId(), credentials.accessKeySecret(), credentials.securityToken());
    }

    private ResolvedCredentials resolveCredentials(AliyunBaseConfig serviceConfig) {
        validateConfig(serviceConfig);
        if (!serviceConfig.useSts()) {
            return new ResolvedCredentials(serviceConfig.getAccessKeyId(), serviceConfig.getAccessKeySecret(), null);
        }
        try {
            return getStsCredential(serviceConfig);
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new IllegalStateException("获取阿里云 STS 临时凭证失败", e);
        }
    }

    private void validateConfig(AliyunBaseConfig serviceConfig) {
        if (serviceConfig == null) {
            throw invalidConfig("阿里云能力配置不能为空");
        }
        if (serviceConfig.getCredentialMode() == null) {
            throw invalidConfig("阿里云能力 credentialMode 不能为空");
        }
        if (isBlank(serviceConfig.getAccessKeyId()) || isBlank(serviceConfig.getAccessKeySecret())) {
            throw invalidConfig("阿里云能力必须配置 accessKeyId 和 accessKeySecret");
        }
        if (serviceConfig.useSts()) {
            if (isBlank(serviceConfig.getRamRoleArn())) {
                throw invalidConfig("阿里云 STS 模式必须配置 ramRoleArn");
            }
            if (serviceConfig.getExpire() == null || serviceConfig.getExpire() <= 0) {
                throw invalidConfig("阿里云 STS 凭证有效期必须大于 0");
            }
        }
    }

    private ResolvedCredentials getStsCredential(AliyunBaseConfig serviceConfig) {
        String stsEndpoint = isBlank(serviceConfig.getStsEndpoint())
                ? "sts.aliyuncs.com" : serviceConfig.getStsEndpoint();
        StsClientKey clientKey = new StsClientKey(serviceConfig.getAccessKeyId(),
                serviceConfig.getAccessKeySecret(), stsEndpoint);
        com.aliyun.sts20150401.Client client = stsClients.computeIfAbsent(clientKey, key -> {
            try {
                com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                        .setAccessKeyId(key.accessKeyId())
                        .setAccessKeySecret(key.accessKeySecret())
                        .setEndpoint(key.endpoint());
                return new com.aliyun.sts20150401.Client(config);
            } catch (Exception e) {
                log.error("AliyunCredentialRegistry.getStsCredential 创建 STS 客户端失败, endpoint={}",
                        key.endpoint(), e);
                throw new IllegalStateException("创建阿里云 STS 客户端失败", e);
            }
        });
        com.aliyun.sts20150401.models.AssumeRoleRequest request = new com.aliyun.sts20150401.models.AssumeRoleRequest()
                .setRoleArn(serviceConfig.getRamRoleArn())
                .setDurationSeconds(serviceConfig.getExpire())
                .setRoleSessionName(UUID.randomUUID().toString());
        try {
            AssumeRoleResponse response = client.assumeRoleWithOptions(request,
                    new com.aliyun.teautil.models.RuntimeOptions());
            if (response == null || response.body == null || response.body.credentials == null) {
                throw new IllegalStateException("阿里云 STS 返回的临时凭证为空");
            }
            AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = response.body.credentials;
            return new ResolvedCredentials(credentials.getAccessKeyId(), credentials.getAccessKeySecret(),
                    credentials.getSecurityToken());
        } catch (TeaException error) {
            log.error("AliyunCredentialRegistry.getStsCredential 获取 STS 临时凭证失败, ramRoleArn={}",
                    serviceConfig.getRamRoleArn(), error);
            throw new IllegalStateException("获取阿里云 STS 临时凭证失败", error);
        } catch (Exception e) {
            log.error("AliyunCredentialRegistry.getStsCredential 获取 STS 临时凭证异常, ramRoleArn={}",
                    serviceConfig.getRamRoleArn(), e);
            throw new IllegalStateException("获取阿里云 STS 临时凭证失败", e);
        }
    }

    private IllegalStateException invalidConfig(String message) {
        log.error("AliyunCredentialRegistry.validateConfig 配置无效, message={}", message);
        return new IllegalStateException(message);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ResolvedCredentials(String accessKeyId, String accessKeySecret, String securityToken) {
    }

    private record StsClientKey(String accessKeyId, String accessKeySecret, String endpoint) {
    }
}
