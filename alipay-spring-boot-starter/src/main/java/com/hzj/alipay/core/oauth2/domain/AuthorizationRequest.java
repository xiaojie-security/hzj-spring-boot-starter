package com.hzj.alipay.core.oauth2.domain;

import com.hzj.alipay.core.oauth2.enums.AlipayOauthScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝网站/APP 授权链接请求参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRequest {

    /**
     * 应用 ID。
     * 为空时默认取全局支付宝配置中的 appId。
     */
    private String appId;

    /**
     * 授权回调地址。
     */
    private String redirectUri;

    /**
     * 授权作用域。
     */
    @Builder.Default
    private AlipayOauthScope scope = AlipayOauthScope.AUTH_USER;

    /**
     * 商户自定义状态串。
     */
    private String state;

    public AuthorizationRequest(String redirectUri, String state) {
        this.redirectUri = redirectUri;
        this.state = state;
        this.scope = AlipayOauthScope.AUTH_USER;
    }
}
