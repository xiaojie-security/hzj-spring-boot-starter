package com.hzj.aliyun.provider.aliyun.imm.entity;

import lombok.Data;

/**
 * IMM 运行时业务配置。
 *
 * <p>媒体处理项目和转码参数支持运行时刷新。</p>
 */
@Data
public class AliyunImmRuntimeConfig {

    /** 媒体处理项目名称。 */
    private String projectName;

    /** 视频编码格式。 */
    private String codec;

    /** 容器格式。 */
    private String container;

    /** 媒体处理服务地址。 */
    private String uri;
}
