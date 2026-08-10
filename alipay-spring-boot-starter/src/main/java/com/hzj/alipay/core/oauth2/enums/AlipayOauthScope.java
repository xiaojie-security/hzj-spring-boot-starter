package com.hzj.alipay.core.oauth2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支付宝 OAuth2 授权范围。
 */
@Getter
@RequiredArgsConstructor
public enum AlipayOauthScope {

    /**
     * 静默授权。
     */
    AUTH_BASE("auth_base"),

    /**
     * 用户信息授权。
     */
    AUTH_USER("auth_user");

    private final String code;
}
