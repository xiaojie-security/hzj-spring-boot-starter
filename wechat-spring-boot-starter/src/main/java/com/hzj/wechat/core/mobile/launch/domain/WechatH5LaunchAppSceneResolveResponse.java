package com.hzj.wechat.core.mobile.launch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 微信 H5 Launch App 场景解析响应。
 */
@Data
@AllArgsConstructor
public class WechatH5LaunchAppSceneResolveResponse {

    /** App 内目标页面路径。 */
    private String targetPath;

    /** 业务对象标识。 */
    private String targetId;

    /** 附加路由数据。 */
    private String extraData;

    /** 场景签发时间，Unix 秒级时间戳。 */
    private Long issuedAt;

    /** 场景过期时间，Unix 秒级时间戳。 */
    private Long expiresAt;
}
