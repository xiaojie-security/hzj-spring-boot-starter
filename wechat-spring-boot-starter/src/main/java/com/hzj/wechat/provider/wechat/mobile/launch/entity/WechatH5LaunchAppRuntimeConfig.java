package com.hzj.wechat.provider.wechat.mobile.launch.entity;

import lombok.Data;

/**
 * 微信 H5 Launch App 运行时业务配置。
 */
@Data
public class WechatH5LaunchAppRuntimeConfig {

    /** 微信 H5 落地页地址。 */
    private String landingPageUrl;

    /** Launch App 场景码有效期，单位秒。 */
    private long sceneTtlSeconds = 300L;

    /** JSAPI Ticket 刷新提前量，单位秒。 */
    private long jsapiTicketRefreshAheadSeconds = 300L;

    /** 微信 JSAPI Ticket 接口地址。 */
    private String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
}
