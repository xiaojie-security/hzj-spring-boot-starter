package com.hzj.wechat.core.access.domain;

import com.hzj.wechat.core.enums.WechatHttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取微信稳定版接口调用凭据请求参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatAccessTokenRequest {

    /**
     * 是否强制刷新接口调用凭据。
     */
    @Builder.Default
    private boolean forceRefresh = false;

    /**
     * 授权类型。
     */
    @Builder.Default
    private String grantType = "client_credential";

    /**
     * 稳定版接口调用凭据接口地址。
     */
    @Builder.Default
    private String requestUrl = "https://api.weixin.qq.com/cgi-bin/stable_token";

    /**
     * 稳定版接口调用凭据接口请求方法。
     */
    @Builder.Default
    private WechatHttpMethod requestMethod = WechatHttpMethod.POST;
}
