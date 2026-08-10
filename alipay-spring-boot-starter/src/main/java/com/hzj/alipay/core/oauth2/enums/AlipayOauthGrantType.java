package com.hzj.alipay.core.oauth2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支付宝 OAuth2 授权方式。
 */
@Getter
@RequiredArgsConstructor
public enum AlipayOauthGrantType {

    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token");

    private final String code;
}
