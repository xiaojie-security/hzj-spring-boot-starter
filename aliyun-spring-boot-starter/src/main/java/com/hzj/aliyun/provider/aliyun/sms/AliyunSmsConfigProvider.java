package com.hzj.aliyun.provider.aliyun.sms;

import com.hzj.aliyun.provider.aliyun.sms.entity.AliyunSmsConfig;
import com.hzj.common.provider.RuntimeConfigProvider;

/**
 * 短信运行时业务配置提供者。
 *
 * <p>凭证、区域和接入点在启动时固定，短信签名参数支持运行时刷新。</p>
 */
public interface AliyunSmsConfigProvider extends RuntimeConfigProvider<AliyunSmsConfig> {
}
