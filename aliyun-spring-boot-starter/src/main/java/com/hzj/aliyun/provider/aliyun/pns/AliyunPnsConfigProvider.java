package com.hzj.aliyun.provider.aliyun.pns;

import com.hzj.aliyun.provider.aliyun.pns.entity.AliyunPnsConfig;
import com.hzj.common.provider.RuntimeConfigProvider;

/**
 * 号码认证服务运行时业务配置提供者。
 *
 * <p>凭证、区域和接入点在启动时固定，验证码业务参数支持运行时刷新。</p>
 */
public interface AliyunPnsConfigProvider extends RuntimeConfigProvider<AliyunPnsConfig> {
}
