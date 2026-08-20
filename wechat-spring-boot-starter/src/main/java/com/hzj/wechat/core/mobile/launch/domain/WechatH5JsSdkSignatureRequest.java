package com.hzj.wechat.core.mobile.launch.domain;

import lombok.Data;

/**
 * 微信 H5 JS-SDK 签名请求。
 */
@Data
public class WechatH5JsSdkSignatureRequest {

    /** 当前 H5 页面完整地址，签名时会自动移除 # 及其后内容。 */
    private String url;

    /** 随机串；为空时由服务自动生成。 */
    private String nonceStr;

    /** 秒级时间戳；为空或非正数时由服务自动生成。 */
    private Long timestamp;
}
