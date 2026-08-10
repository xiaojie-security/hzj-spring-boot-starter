package com.aliyun.properties;

import com.aliyun.provider.aliyun.oss.enums.AliyunOssPermission;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


/**
 * 阿里云 OSS 对象存储配置类
 * 用于封装 OSS 服务的连接信息、认证配置和存储桶设置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssProperties extends AliyunBaseProperties {

    /**
     * OSS 服务接入点地址
     * 指定 OSS 服务的地域节点 Endpoint
     */
    private String endpoint;

    /**
     * OSS 对象访问权限。
     */
    private AliyunOssPermission permission = AliyunOssPermission.PRIVATE;

    /**
     * OSS 自定义访问域名，不需要携带请求路径。
     */
    private String domain;

    /**
     * 是否使用 HTTPS 访问 OSS 对象。
     */
    private Boolean https = Boolean.TRUE;

    /**
     * OSS 服务的区域
     * 存储桶所在的区域
     */
    private String region;

    /**
     * OSS 存储桶映射配置
     * key 为业务标识，value 为实际的 Bucket 名称
     */
    private Map<String,String> buckets;

    /**
     * 默认存储桶名称
     * 当未指定具体 Bucket 时使用的默认存储桶
     */
    private String defaultBucket;

    /**
     * 临时凭证有效期（秒）
     * 临时凭证的有效使用时间范围
     */
    private Long expire;

    /**
     * 设置上传回调URL（即回调服务器地址），必须为公网地址。用于处理应用服务器与OSS之间的通信，OSS会在文件上传完成后，把文件上传信息通过此回调URL发送给应用服务器。
     */
    private String callback;


}

