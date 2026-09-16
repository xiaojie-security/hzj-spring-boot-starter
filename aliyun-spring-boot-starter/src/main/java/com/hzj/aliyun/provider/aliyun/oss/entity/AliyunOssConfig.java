package com.hzj.aliyun.provider.aliyun.oss.entity;

import com.hzj.aliyun.provider.aliyun.common.entity.AliyunBaseConfig;
import com.hzj.aliyun.provider.aliyun.oss.enums.AliyunOssPermission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * OSS 配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AliyunOssConfig extends AliyunBaseConfig {

    /** OSS 启动期接入点，客户端创建后不支持动态刷新。 */
    private String endpoint;
    /** OSS 访问权限。 */
    private AliyunOssPermission permission = AliyunOssPermission.PRIVATE;
    /** 自定义访问域名，不包含请求路径。 */
    private String domain;
    /** 是否使用 HTTPS 访问。 */
    private Boolean https = Boolean.TRUE;
    /** OSS 启动期服务区域，客户端创建后不支持动态刷新。 */
    private String region;
    /** 业务标识到存储桶的映射。 */
    private Map<String, String> buckets;
    /** 默认存储桶。 */
    private String defaultBucket;
    /** OSS 签名 URL 有效期，单位为秒。 */
    private Long signedUrlDurationSeconds = 3600L;
    /** 上传回调地址。 */
    private String callback;
}
