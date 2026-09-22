package com.hzj.aliyun.config;

import com.hzj.aliyun.core.ocr.AliyunOcrService;
import com.hzj.aliyun.provider.aliyun.ocr.AliyunOcrRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.ocr.AliyunOcrStaticConfigProvider;
import com.hzj.aliyun.provider.aliyun.ocr.entity.AliyunOcrRuntimeConfig;
import com.hzj.aliyun.provider.aliyun.ocr.entity.AliyunOcrStaticConfig;
import com.hzj.aliyun.utils.AliyunCredentialRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 阿里云 OCR 自动装配测试。
 */
class AliyunOcrConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AliyunOcrConfiguration.class));

    @Test
    void shouldNotLoadOcrBeansWhenProvidersMissing() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(AliyunOcrService.class);
            assertThat(context).doesNotHaveBean("aliyunOcrClient");
        });
    }

    @Test
    void shouldLoadOcrBeansWhenProvidersPresent() {
        contextRunner.withUserConfiguration(OcrProviderConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(AliyunOcrService.class);
            assertThat(context).hasBean("aliyunOcrClient");
            assertThat(context.getBean(AliyunOcrService.class).getClass().getSimpleName())
                    .isEqualTo("DefaultAliyunOcrService");
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class OcrProviderConfiguration {

        @Bean
        AliyunCredentialRegistry credentialRegistry() {
            return new AliyunCredentialRegistry();
        }

        @Bean
        AliyunOcrStaticConfigProvider aliyunOcrStaticConfigProvider() {
            AliyunOcrStaticConfig config = new AliyunOcrStaticConfig();
            config.getCredential().setAccessKeyId("test-access-key-id");
            config.getCredential().setAccessKeySecret("test-access-key-secret");
            return () -> config;
        }

        @Bean
        AliyunOcrRuntimeConfigProvider aliyunOcrRuntimeConfigProvider() {
            return AliyunOcrRuntimeConfig::new;
        }
    }
}
