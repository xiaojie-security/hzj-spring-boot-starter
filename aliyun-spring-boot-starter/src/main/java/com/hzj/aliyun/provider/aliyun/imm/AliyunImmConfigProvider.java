package com.hzj.aliyun.provider.aliyun.imm;

import com.hzj.aliyun.provider.aliyun.imm.entity.AliyunImmConfig;
import com.hzj.common.provider.RuntimeConfigProvider;

/**
 * IMM 运行时业务配置提供者。
 *
 * <p>凭证、区域和接入点在启动时固定，媒体处理业务参数支持运行时刷新。</p>
 */
public interface AliyunImmConfigProvider extends RuntimeConfigProvider<AliyunImmConfig> {
}
