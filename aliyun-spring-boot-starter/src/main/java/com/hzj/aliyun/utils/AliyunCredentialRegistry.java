package com.hzj.aliyun.utils;

import com.hzj.aliyun.provider.aliyun.common.entity.AliyunCredentialConfig;
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
     * @param credentialConfig 当前阿里云能力的启动期凭证快照
     * @return OpenAPI 配置
     */
    public com.aliyun.teaopenapi.models.Config createOpenApiConfig(AliyunCredentialConfig credentialConfig) {
        ResolvedCredentials credentials = resolveCredentials(credentialConfig);
        com.aliyun.teaopenapi.models.Config openApiConfig = new com.aliyun.teaopenapi.models.Config();
        openApiConfig.setAccessKeyId(credentials.accessKeyId());
        openApiConfig.setAccessKeySecret(credentials.accessKeySecret());
        openApiConfig.setSecurityToken(credentials.securityToken());
        return openApiConfig;
    }

    /**
     * 获取 OSS V2 使用的凭证。
     *
     * @param credentialConfig OSS 凭证配置快照
     * @return OSS V2 凭证
     */
    public com.aliyun.sdk.service.oss2.credentials.Credentials getOssV2Credentials(
            AliyunCredentialConfig credentialConfig) {
        ResolvedCredentials credentials = resolveCredentials(credentialConfig);
        return new com.aliyun.sdk.service.oss2.credentials.Credentials(
                credentials.accessKeyId(), credentials.accessKeySecret(), credentials.securityToken());
    }

    /**
     * 获取旧版 OSS 使用的凭证。
     *
     * @param credentialConfig OSS 凭证配置快照
     * @return OSS 凭证
     */
    public com.aliyun.oss.common.auth.Credentials getOssCredentials(AliyunCredentialConfig credentialConfig) {
        ResolvedCredentials credentials = resolveCredentials(credentialConfig);
        if (credentials.securityToken() == null) {
            return new com.aliyun.oss.common.auth.DefaultCredentials(
                    credentials.accessKeyId(), credentials.accessKeySecret());
        }
        return new com.aliyun.oss.common.auth.DefaultCredentials(
                credentials.accessKeyId(), credentials.accessKeySecret(), credentials.securityToken());
    }

    /**
     * 获取 OSS POST 直传签名所需的凭证快照。
     *
     * <p>旧版 OSS SDK 的 {@code Credentials} 接口在不同版本中 getter 命名不一致，
     * 业务层不应因此依赖 SDK 细节或重新解析 AccessKey。</p>
     *
     * @param credentialConfig 当前 OSS 能力的启动期凭证配置快照
     * @return OSS 上传凭证快照
     */
    public OssUploadCredentials getOssUploadCredentials(AliyunCredentialConfig credentialConfig) {
        ResolvedCredentials credentials = resolveCredentials(credentialConfig);
        return new OssUploadCredentials(credentials.accessKeyId(), credentials.accessKeySecret(),
                credentials.securityToken());
    }

    /** OSS 上传签名使用的凭证快照。 */
    public record OssUploadCredentials(String accessKeyId, String accessKeySecret, String securityToken) {
    }

    private ResolvedCredentials resolveCredentials(AliyunCredentialConfig credentialConfig) {
        validateConfig(credentialConfig);
        if (!credentialConfig.useSts()) {
            return new ResolvedCredentials(credentialConfig.getAccessKeyId(),
                    credentialConfig.getAccessKeySecret(), null);
        }
        try {
            return getStsCredential(credentialConfig);
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new IllegalStateException("获取阿里云 STS 临时凭证失败", e);
        }
    }

    private void validateConfig(AliyunCredentialConfig credentialConfig) {
        if (credentialConfig == null) {
            throw invalidConfig("阿里云能力配置不能为空");
        }
        if (credentialConfig.getCredentialMode() == null) {
            throw invalidConfig("阿里云能力 credentialMode 不能为空");
        }
        if (isBlank(credentialConfig.getAccessKeyId()) || isBlank(credentialConfig.getAccessKeySecret())) {
            throw invalidConfig("阿里云能力必须配置 accessKeyId 和 accessKeySecret");
        }
        if (credentialConfig.useSts()) {
            if (isBlank(credentialConfig.getRamRoleArn())) {
                throw invalidConfig("阿里云 STS 模式必须配置 ramRoleArn");
            }
            if (credentialConfig.getStsDurationSeconds() == null
                    || credentialConfig.getStsDurationSeconds() <= 0) {
                throw invalidConfig("阿里云 STS 凭证有效期必须大于 0");
            }
        }
    }

    private ResolvedCredentials getStsCredential(AliyunCredentialConfig credentialConfig) {
        String stsEndpoint = isBlank(credentialConfig.getStsEndpoint())
                ? "sts.aliyuncs.com" : credentialConfig.getStsEndpoint();
        StsClientKey clientKey = new StsClientKey(credentialConfig.getAccessKeyId(),
                credentialConfig.getAccessKeySecret(), stsEndpoint);
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
                .setRoleArn(credentialConfig.getRamRoleArn())
                .setDurationSeconds(credentialConfig.getStsDurationSeconds())
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
                    credentialConfig.getRamRoleArn(), error);
            throw new IllegalStateException("获取阿里云 STS 临时凭证失败", error);
        } catch (Exception e) {
            log.error("AliyunCredentialRegistry.getStsCredential 获取 STS 临时凭证异常, ramRoleArn={}",
                    credentialConfig.getRamRoleArn(), e);
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
