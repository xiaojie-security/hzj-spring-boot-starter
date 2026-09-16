package com.hzj.wechat.provider.wechat.safety_control.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信安全风控运行时业务配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatSafetyControlRuntimeConfig {

    /** 是否测试调用，false 正式调用，true 测试调用，默认 false。 */
    private Boolean isTest = false;
}
