package com.hzj.wechat.core.mobile.launch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 微信 H5 JS-SDK 配置签名响应。
 */
@Data
@AllArgsConstructor
public class WechatH5JsSdkSignatureResponse {

    /** 认证服务号 AppID。 */
    private String appid;

    /** 秒级时间戳。 */
    private Long timestamp;

    /** 随机串。 */
    private String nonceStr;

    /** JS-SDK SHA-1 签名。 */
    private String signature;

    /** H5 页面需要注册的微信开放标签。 */
    private List<String> openTagList;
}
