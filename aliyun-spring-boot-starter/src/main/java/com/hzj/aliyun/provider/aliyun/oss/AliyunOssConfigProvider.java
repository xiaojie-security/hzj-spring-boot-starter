package com.hzj.aliyun.provider.aliyun.oss;

import com.hzj.aliyun.provider.aliyun.oss.entity.AliyunOssConfig;
import com.hzj.common.provider.RuntimeConfigProvider;

/**
 * OSS 运行时业务配置提供者。
 *
 * <p>凭证、区域和接入点在启动时固定，桶映射、域名、权限和回调参数支持运行时刷新。</p>
 */
public interface AliyunOssConfigProvider extends RuntimeConfigProvider<AliyunOssConfig> {
}
