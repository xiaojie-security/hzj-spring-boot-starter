package com.hzj.amap.provider.webapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebApiConfig {

    /**
     * 高德 Web服务 API 密钥（key）
     */
    private String secretKey;
}
