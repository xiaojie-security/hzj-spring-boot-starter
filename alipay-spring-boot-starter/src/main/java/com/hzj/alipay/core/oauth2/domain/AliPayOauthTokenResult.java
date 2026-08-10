package com.hzj.alipay.core.oauth2.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 支付宝 OAuth2 令牌结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayOauthTokenResult {

    /**
     * 访问令牌。
     */
    private String accessToken;

    /**
     * 刷新令牌。
     */
    private String refreshToken;

    /**
     * openId。
     */
    private String openId;

    /**
     * 支付宝用户 ID。
     */
    private String userId;

    /**
     * unionId。
     */
    private String unionId;

    /**
     * 访问令牌有效期，单位秒。
     */
    private String expiresIn;

    /**
     * 刷新令牌有效期，单位秒。
     */
    private String reExpiresIn;

    /**
     * 令牌类型。
     */
    private String authTokenType;

    /**
     * 授权开始时间。
     */
    private Date authStart;
}
