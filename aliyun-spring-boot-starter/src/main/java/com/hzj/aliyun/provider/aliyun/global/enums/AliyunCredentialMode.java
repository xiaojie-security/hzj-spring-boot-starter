package com.hzj.aliyun.provider.aliyun.global.enums;

/**
 * 阿里云访问凭证模式。
 */
public enum AliyunCredentialMode {

    /** 使用 STS 临时访问凭证。 */
    STS,
    /** 使用 AccessKey 固定访问凭证。 */
    AK
}
