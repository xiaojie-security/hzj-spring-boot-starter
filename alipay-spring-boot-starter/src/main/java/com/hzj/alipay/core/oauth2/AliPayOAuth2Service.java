package com.hzj.alipay.core.oauth2;

import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.hzj.alipay.core.oauth2.domain.AliPayOauthTokenResult;
import com.hzj.alipay.core.oauth2.domain.AuthorizationRequest;

public interface AliPayOAuth2Service {

    /**
     * 生成支付宝授权链接。
     *
     * @param request 授权请求参数
     * @return 授权链接
     */
    String generateAuthUrl(AuthorizationRequest request);

    /**
     * 查询用户授权信息。
     *
     * @param accessToken 用户访问令牌
     * @return 用户授权信息
     */
    AlipayUserInfoShareResponse queryUserInfoShare(String accessToken);

    /**
     * 通过授权码换取令牌。
     *
     * @param authorizationCode 授权码
     * @return OAuth2 令牌信息
     */
    AliPayOauthTokenResult getAccessTokenByCode(String authorizationCode);

    /**
     * 通过刷新令牌换取新令牌。
     *
     * @param refreshToken 刷新令牌
     * @return OAuth2 令牌信息
     */
    AliPayOauthTokenResult refreshAccessToken(String refreshToken);
}
