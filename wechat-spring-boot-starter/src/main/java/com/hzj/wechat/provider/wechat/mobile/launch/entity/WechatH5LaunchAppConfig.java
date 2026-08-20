package com.hzj.wechat.provider.wechat.mobile.launch.entity;

import lombok.Data;

/**
 * 微信 H5 拉起 App 动态配置。
 */
@Data
public class WechatH5LaunchAppConfig {

    /** 微信开放平台移动应用 AppID。 */
    private String appid;

    /** 认证服务号 AppID，用于微信 H5 JS-SDK 配置。 */
    private String jsSdkAppid;

    /** 微信 H5 落地页地址。 */
    private String landingPageUrl;

    /** Launch App 场景码 HMAC 签名密钥。 */
    private String sceneSigningSecret;

    /** Launch App 场景码有效期，单位秒。 */
    private long sceneTtlSeconds = 300L;

    /** JSAPI Ticket 刷新提前量，单位秒。 */
    private long jsapiTicketRefreshAheadSeconds = 300L;

    /** 微信 JSAPI Ticket 接口地址。 */
    private String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
}
