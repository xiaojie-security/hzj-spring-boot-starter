package com.hzj.aliyun.provider.aliyun.common.enums;

/**
 * 阿里云访问凭证模式。
 */
public enum AliyunCredentialMode {

    /** 使用 AccessKey 固定访问凭证。 */
    AK,

    /** 使用 STS 临时访问凭证。 */
    STS
}
