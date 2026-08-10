package com.aliyun.provider.aliyun.sts.entity;

import lombok.Data;

/**
 * STS 配置。
 */
@Data
public class AliyunStsConfig {

    /** STS 服务接入点。 */
    private String endpoint;
    /** 临时凭证有效期，单位为秒。 */
    private Long expire = 3600L;
}
