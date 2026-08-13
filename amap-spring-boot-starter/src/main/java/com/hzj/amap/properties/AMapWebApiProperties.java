package com.hzj.amap.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 高德 Web 服务 API 配置属性。
 */
@Data
@ConfigurationProperties(prefix = "amap.webapi")
public class AMapWebApiProperties {

    /**
     * 高德 Web 服务 API 密钥。
     */
    private String secretKey;
}
