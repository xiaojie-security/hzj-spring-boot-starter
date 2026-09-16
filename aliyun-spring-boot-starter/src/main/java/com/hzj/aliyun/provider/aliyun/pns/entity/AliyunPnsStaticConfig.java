package com.hzj.aliyun.provider.aliyun.pns.entity;

import com.hzj.aliyun.provider.aliyun.common.entity.AliyunBaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 号码认证服务启动期静态配置。
 *
 * <p>凭证、接入点和区域用于创建客户端，应用启动后不支持动态刷新。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AliyunPnsStaticConfig extends AliyunBaseConfig {

    /** 服务接入点。 */
    private String endpoint;

    /** 服务区域。 */
    private String region;
}
