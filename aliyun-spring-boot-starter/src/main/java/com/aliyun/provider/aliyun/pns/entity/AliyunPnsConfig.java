package com.aliyun.provider.aliyun.pns.entity;

import com.aliyun.provider.aliyun.global.entity.AliyunBaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 号码认证服务配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AliyunPnsConfig extends AliyunBaseConfig {

    /** 服务签名。 */
    private String signName;
    /** 服务接入点。 */
    private String endpoint;
    /** 服务区域。 */
    private String region;

    /** 验证码长度。 */
    private Long codeLength = 6L;

    /** 验证码有效时间，单位为秒。 */
    private Long validTime = 300L;

    /** 重复发送策略。 */
    private Long duplicatePolicy = 1L;

    /** 验证码发送间隔，单位为秒。 */
    private Long interval = 60L;

    /** 验证码类型。 */
    private Long codeType = 1L;

    /** 是否返回验证码。 */
    private Boolean returnVerifyCode = Boolean.TRUE;

    /** 是否自动重试。 */
    private Long autoRetry = 1L;

    /** 国家代码。 */
    private String countryCode = "86";
}
