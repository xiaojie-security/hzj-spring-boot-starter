package com.hzj.wechat.core.oauth2.domain;

import com.hzj.wechat.core.enums.WechatHttpMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 校验网站授权 access_token 请求参数类
 *
 * @author YourName
 * @version 1.0
 * @since 2026-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenValidateRequest {

    /**
     * 网站授权接口调用凭证
     */
    private String accessToken;

    /**
     * 授权用户唯一标识
     */
    private String openid;

    /**
     * 网站应用 access_token 校验接口地址。
     */
    @Builder.Default
    private String requestUrl = "https://api.weixin.qq.com/sns/auth";

    /**
     * 网站应用 access_token 校验接口请求方法。
     */
    @Builder.Default
    private WechatHttpMethod requestMethod = WechatHttpMethod.GET;
}
