package com.hzj.aliyun.provider.aliyun.global.entity;

import lombok.Data;

/**
 * 阿里云服务通用配置。
 */
@Data
public class AliyunBaseConfig {

    /**
     * 当前服务使用的 RAM 角色 ARN。
     */
    private String ramRoleArn;
}
