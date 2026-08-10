package com.hzj.aliyun.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云 STS 临时访问凭证授权配置类
 * 用于封装获取 STS Token 所需的认证信息和配置参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ConfigurationProperties(prefix = "aliyun.sts")
public class AliyunStsProperties extends AliyunBaseProperties {

    /**
     * STS 服务接入点地址
     * 用于连接阿里云 STS 服务的 Endpoint
     */
    private String endpoint;

    /**
     * 临时凭证有效期（秒）
     * STS Token 的有效使用时间范围
     */
    private Long expire = 3600L;

}
