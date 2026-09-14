package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.Expose;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 微信内容安全 API 请求基础参数。
 */
public class WechatXcxSecCheckApiRequest {

    /**
     * 微信开放接口主机地址。
     */
    @Expose(serialize = false)
    public String requestHost = "https://api.weixin.qq.com";

    /**
     * 微信开放接口请求路径。
     */
    @Expose(serialize = false)
    public String requestPath;

    /**
     * 微信开放接口请求方法。
     */
    @Expose(serialize = false)
    public WechatHttpMethod requestMethod;
}
