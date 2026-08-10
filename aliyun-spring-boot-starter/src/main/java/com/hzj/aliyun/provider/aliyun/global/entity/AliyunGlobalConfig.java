package com.hzj.aliyun.provider.aliyun.global.entity;

import com.hzj.aliyun.provider.aliyun.global.enums.AliyunCredentialMode;
import lombok.Data;

/**
 * 阿里云全局凭证配置。
 */
@Data
public class AliyunGlobalConfig {

    /** 访问凭证模式，默认使用 STS。 */
    private AliyunCredentialMode credentialMode = AliyunCredentialMode.STS;
    /** 全局 AccessKey ID。 */
    private String accessKeyId;
    /** 全局 AccessKey Secret。 */
    private String accessKeySecret;
    /** STS 临时凭证有效期，单位为秒。 */
    private Long expire = 3600L;

    /**
     * 判断当前是否使用 STS。
     *
     * @return true-使用 STS，false-使用固定 AK
     */
    public boolean useSts() {
        return credentialMode == AliyunCredentialMode.STS;
    }
}
