package com.hzj.wechat.core.mobile.launch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 微信 H5 Launch App 场景签发响应。
 */
@Data
@AllArgsConstructor
public class WechatH5LaunchAppSceneResponse {

    /** 短期场景码。 */
    private String scene;

    /** 携带场景码的微信 H5 落地页地址。 */
    private String landingPageUrl;

    /** 微信开放平台移动应用 AppID。 */
    private String appid;

    /** 用于 wx-open-launch-app extinfo 的场景参数。 */
    private String extInfo;

    /** 场景码过期时间，Unix 秒级时间戳。 */
    private Long expiresAt;
}
