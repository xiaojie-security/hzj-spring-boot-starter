package com.aliyun.properties;

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

}


