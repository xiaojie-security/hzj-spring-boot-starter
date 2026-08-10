package com.aliyun.properties;

import com.aliyun.provider.aliyun.global.enums.AliyunCredentialMode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云全局凭证配置属性。
 */
@Data
@ConfigurationProperties(prefix = "aliyun")
public class AliyunGlobalProperties {

    /** 访问凭证模式。 */
    private AliyunCredentialMode credentialMode = AliyunCredentialMode.STS;
    /** 全局 AccessKey ID。 */
    private String accessKeyId;
    /** 全局 AccessKey Secret。 */
    private String accessKeySecret;
    /** STS 临时凭证有效期，单位为秒。 */
    private Long expire = 3600L;
}
