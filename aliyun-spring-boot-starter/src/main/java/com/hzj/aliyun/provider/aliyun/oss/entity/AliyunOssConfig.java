package com.hzj.aliyun.provider.aliyun.oss.entity;

import com.hzj.aliyun.provider.aliyun.global.entity.AliyunBaseConfig;
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

    /** OSS 接入点。 */
    private String endpoint;
    /** OSS 访问权限。 */
    private AliyunOssPermission permission = AliyunOssPermission.PRIVATE;
    /** 自定义访问域名，不包含请求路径。 */
    private String domain;
    /** 是否使用 HTTPS 访问。 */
    private Boolean https = Boolean.TRUE;
    /** OSS 服务区域。 */
    private String region;
    /** 业务标识到存储桶的映射。 */
    private Map<String, String> buckets;
    /** 默认存储桶。 */
    private String defaultBucket;
    /** 临时凭证有效期，单位为秒。 */
    private Long expire;
    /** 上传回调地址。 */
    private String callback;
}
