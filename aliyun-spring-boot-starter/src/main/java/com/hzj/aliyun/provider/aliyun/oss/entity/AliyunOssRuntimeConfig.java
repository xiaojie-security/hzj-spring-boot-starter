package com.hzj.aliyun.provider.aliyun.oss.entity;

import com.hzj.aliyun.provider.aliyun.oss.enums.AliyunOssPermission;
import lombok.Data;

import java.util.Map;

/**
 * OSS 运行时业务配置。
 *
 * <p>该配置用于存储桶选择、访问地址和上传签名等业务行为，支持运行时刷新。</p>
 */
@Data
public class AliyunOssRuntimeConfig {

    /** OSS 访问权限。 */
    private AliyunOssPermission permission = AliyunOssPermission.PRIVATE;

    /** 自定义访问域名，不包含请求路径。 */
    private String domain;

    /** 是否使用 HTTPS 访问。 */
    private Boolean https = Boolean.TRUE;

    /** 业务标识到存储桶的映射。 */
    private Map<String, String> buckets;

    /** 默认存储桶。 */
    private String defaultBucket;

    /** OSS 签名 URL 有效期，单位为秒。 */
    private Long signedUrlDurationSeconds = 3600L;

    /** 上传回调地址。 */
    private String callback;
}
