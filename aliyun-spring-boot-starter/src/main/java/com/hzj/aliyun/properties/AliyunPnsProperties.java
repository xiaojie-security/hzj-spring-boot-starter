package com.hzj.aliyun.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云号码认证服务配置类
 *
 * <p>用于封装阿里云短信服务的相关配置信息，包括签名、接入地址和模板编码等</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ConfigurationProperties(prefix = "aliyun.pns")
public class AliyunPnsProperties extends AliyunBaseProperties {

    /**
     * 签名
     */
    private String signName;

    /**
     * 号码认证服务接入端点
     */
    private String endpoint;

    /**
     * 号码认证服务接入区域
     */
    private String region;

    /**
     * 验证码长度。
     */
    private Long codeLength = 6L;

    /**
     * 验证码有效时间，单位为秒。
     */
    private Long validTime = 300L;

    /**
     * 重复发送策略。
     */
    private Long duplicatePolicy = 1L;

    /**
     * 验证码发送间隔，单位为秒。
     */
    private Long interval = 60L;

    /**
     * 验证码类型。
     */
    private Long codeType = 1L;

    /**
     * 是否返回验证码。
     */
    private Boolean returnVerifyCode = Boolean.TRUE;

    /**
     * 是否自动重试。
     */
    private Long autoRetry = 1L;

    /**
     * 国家代码。
     */
    private String countryCode = "86";

}


