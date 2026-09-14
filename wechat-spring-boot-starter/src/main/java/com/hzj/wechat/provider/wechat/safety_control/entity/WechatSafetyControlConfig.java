package com.hzj.wechat.provider.wechat.safety_control.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信安全风控动态配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatSafetyControlConfig {

    /**
     * 小程序 appid，获取用户安全等级接口为必填参数。
     */
    private String appid;

    /**
     * 是否测试调用，false 正式调用，true 测试调用，默认 false。
     */
    private Boolean isTest = false;
}
