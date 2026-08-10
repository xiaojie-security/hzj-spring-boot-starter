package com.aliyun.provider.aliyun.pns.entity;

import com.aliyun.provider.aliyun.global.entity.AliyunBaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 号码认证服务配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AliyunPnsConfig extends AliyunBaseConfig {

    /** 服务签名。 */
    private String signName;
    /** 服务接入点。 */
    private String endpoint;
    /** 服务区域。 */
    private String region;
}
