package com.hzj.wechat.provider.wechat.mobile.launch.entity;

import lombok.Data;

/**
 * 微信 H5 Launch App 启动期静态配置。
 *
 * <p>应用标识和场景签名密钥属于身份配置，应用启动后不支持动态刷新。</p>
 */
@Data
public class WechatH5LaunchAppStaticConfig {

    /** 微信开放平台移动应用 AppID。 */
    private String appid;

    /** 认证服务号 AppID，用于微信 H5 JS-SDK 配置。 */
    private String jsSdkAppid;

    /** Launch App 场景码 HMAC 签名密钥。 */
    private String sceneSigningSecret;
}
