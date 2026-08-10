package com.hzj.aliyun.properties;

import lombok.Data;

/**
 * 阿里云服务属性基类。
 *
 * <p>服务开关由 {@code ConditionalOnProperty} 处理，凭证由全局 Provider 统一管理。</p>
 */
@Data
public class AliyunBaseProperties {

    /**
     * 当前服务使用的 RAM 角色 ARN。
     */
    protected String ramRoleArn;
}
