package com.hzj.alipay.core.oauth2.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 支付宝系统 OAuth 授权详情。
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AliPaySystemOauthDetails {

    /** 认证令牌。 */
    private String accessToken;

    /** 刷新令牌。 */
    private String refreshToken;

    /** openId。 */
    private String openId;
}
