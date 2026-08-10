package com.hzj.aliyun.provider.aliyun.imm.entity;

import com.hzj.aliyun.provider.aliyun.global.entity.AliyunBaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * IMM 配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AliyunImmConfig extends AliyunBaseConfig {

    /** 媒体处理项目名称。 */
    private String projectName;
    /** 服务区域。 */
    private String region;
    /** 视频编码格式。 */
    private String codec;
    /** 自定义服务接入点。 */
    private String endpointOverride;
    /** 容器格式。 */
    private String container;
    /** 媒体处理服务地址。 */
    private String uri;
}
