package com.hzj.wechat.core.mobile.launch.domain;

import lombok.Data;

/**
 * 微信 H5 Launch App 场景签发请求。
 */
@Data
public class WechatH5LaunchAppSceneIssueRequest {

    /** App 内目标页面路径。 */
    private String targetPath;

    /** 业务对象标识，例如视频 ID 或内容 ID。 */
    private String targetId;

    /** 附加路由数据；不得放入敏感信息，场景码内容可被 Base64 解码。 */
    private String extraData;
}
