package com.hzj.wechat.provider.wechat.safety_control.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信安全风控启动期静态配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatSafetyControlStaticConfig {

    /** 小程序 appid，获取用户安全等级接口为必填参数。 */
    private String appid;
}
