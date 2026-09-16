package com.hzj.aliyun.utils;

import com.hzj.aliyun.provider.aliyun.common.entity.AliyunCredentialConfig;
import com.hzj.aliyun.provider.aliyun.common.entity.AliyunBaseConfig;
import com.hzj.aliyun.provider.aliyun.common.enums.AliyunCredentialMode;
import com.hzj.aliyun.provider.aliyun.oss.entity.AliyunOssStaticConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * 阿里云能力级凭证解析测试。
 */
class AliyunCredentialRegistryTest {

    private final AliyunCredentialRegistry registry = new AliyunCredentialRegistry();

    /**
     * 验证不同能力使用各自的固定 AK。
     */
    @Test
    void shouldResolveCredentialsFromEachServiceConfig() {
        AliyunCredentialConfig smsConfig = createAkConfig("sms-access-key-id", "sms-access-key-secret");
        AliyunCredentialConfig ossConfig = createAkConfig("oss-access-key-id", "oss-access-key-secret");

        com.aliyun.teaopenapi.models.Config smsClientConfig = registry.createOpenApiConfig(smsConfig);
        com.aliyun.teaopenapi.models.Config ossClientConfig = registry.createOpenApiConfig(ossConfig);

        assertThat(smsClientConfig.getAccessKeyId()).isEqualTo("sms-access-key-id");
        assertThat(smsClientConfig.getAccessKeySecret()).isEqualTo("sms-access-key-secret");
        assertThat(ossClientConfig.getAccessKeyId()).isEqualTo("oss-access-key-id");
        assertThat(ossClientConfig.getAccessKeySecret()).isEqualTo("oss-access-key-secret");
        assertThat(smsClientConfig.getAccessKeyId()).isNotEqualTo(ossClientConfig.getAccessKeyId());
    }

    /**
     * 验证没有配置全局 Provider 时，基础自动配置仍可以提供注册器。
     */
    @Test
    void shouldRegisterCredentialRegistryWithoutGlobalProvider() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(com.hzj.aliyun.config.AliyunConfiguration.class))
                .run(context -> assertThat(context).hasSingleBean(AliyunCredentialRegistry.class));
    }

    /**
     * 验证 STS 模式不能缺少当前能力自己的 RAM 角色。
     */
    @Test
    void shouldValidateStsCredentialByServiceConfig() {
        AliyunCredentialConfig config = createAkConfig("source-access-key-id", "source-access-key-secret");
        config.setCredentialMode(AliyunCredentialMode.STS);

        assertThatIllegalStateException()
                .isThrownBy(() -> registry.createOpenApiConfig(config))
                .withMessage("阿里云 STS 模式必须配置 ramRoleArn");
    }

    /**
     * 验证能力配置中的启动期凭证不会被运行时配置变更覆盖。
     */
    @Test
    void shouldKeepCredentialSnapshotAfterRuntimeConfigChanges() {
        AliyunBaseConfig serviceConfig = new AliyunOssStaticConfig();
        serviceConfig.getCredential().setAccessKeyId("startup-access-key-id");
        serviceConfig.getCredential().setAccessKeySecret("startup-access-key-secret");

        AliyunCredentialConfig snapshot = serviceConfig.snapshotCredentialConfig();
        serviceConfig.getCredential().setAccessKeyId("runtime-access-key-id");
        serviceConfig.getCredential().setAccessKeySecret("runtime-access-key-secret");

        assertThat(snapshot.getAccessKeyId()).isEqualTo("startup-access-key-id");
        assertThat(snapshot.getAccessKeySecret()).isEqualTo("startup-access-key-secret");
    }

    private AliyunCredentialConfig createAkConfig(String accessKeyId, String accessKeySecret) {
        AliyunCredentialConfig config = new AliyunCredentialConfig();
        config.setCredentialMode(AliyunCredentialMode.AK);
        config.setAccessKeyId(accessKeyId);
        config.setAccessKeySecret(accessKeySecret);
        return config;
    }
}
