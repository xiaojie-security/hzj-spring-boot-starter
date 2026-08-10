package com.aliyun.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 阿里云短信服务配置类
 *
 * <p>用于封装阿里云短信服务的相关配置信息，包括签名、接入地址和模板编码等</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ConfigurationProperties(prefix = "aliyun.sms")
public class AliyunSmsProperties extends AliyunBaseProperties {

    /**
     * 短信服务接入端点
     */
    private String endpoint;

    /**
     * 短信服务接入区域
     */
    private String region;

    /**
     * 短信签名映射
     */
    private Map<String, String> signNames;

    /**
     * 默认签名
     */
    private String defaultSignName;

    public String getSignName() {
        return defaultSignName;
    }

    public String getSignName(String signKey) {
        if (CollUtil.isEmpty(signNames)) {
            return defaultSignName;
        }
        return signNames.getOrDefault(signKey, defaultSignName);
    }

}


