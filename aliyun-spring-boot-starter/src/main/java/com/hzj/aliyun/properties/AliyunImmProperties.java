package com.hzj.aliyun.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 阿里云 OSS 媒体转码配置类
 * 用于封装 OSS 媒体处理服务的转码参数和任务配置信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ConfigurationProperties(prefix = "aliyun.imm")
public class AliyunImmProperties extends AliyunBaseProperties {

    /**
     * 媒体处理项目名称
     * 标识转码任务所属的项目
     */
    private String projectName;

    /**
     * 服务区域
     * 指定媒体处理服务所在的地域
     */
    private String region;

    /**
     * 视频编码格式
     */
    private String codec;

    /**
     * 自定义服务接入点地址
     * 用于覆盖默认配置的服务 Endpoint，支持指定特定的服务节点
     */
    private String endpointOverride;

    /**
     * 容器格式
     * 指定媒体文件的容器封装格式
     */
    private String container;

    /**
     * 媒体处理服务地址
     */
    private String uri;

}

