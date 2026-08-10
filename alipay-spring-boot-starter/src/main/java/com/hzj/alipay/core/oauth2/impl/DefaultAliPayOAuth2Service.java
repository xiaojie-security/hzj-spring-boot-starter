package com.hzj.alipay.core.oauth2.impl;

import cn.hutool.core.net.url.UrlBuilder;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.hzj.alipay.core.AbstractAlipayService;
import com.hzj.alipay.core.oauth2.AliPayOAuth2Service;
import com.hzj.alipay.core.oauth2.domain.AliPayOauthTokenResult;
import com.hzj.alipay.core.oauth2.domain.AuthorizationRequest;
import com.hzj.alipay.core.oauth2.enums.AlipayOauthGrantType;
import com.hzj.alipay.core.oauth2.enums.AlipayOauthScope;
import com.hzj.alipay.core.AliPayException;
import com.hzj.alipay.provider.AlipayConfigProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class DefaultAliPayOAuth2Service extends AbstractAlipayService implements AliPayOAuth2Service {
    private static final String AUTH_URL = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";
    private final AlipayConfigProvider provider;
    /**
     * 生成支付宝授权地址。
     *
     * @param request 授权请求参数
     * @return 授权地址
     */
    @Override
    public String generateAuthUrl(AuthorizationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("授权请求参数不能为空");
        }
        if (!StringUtils.hasText(request.getRedirectUri())) {
            throw new IllegalArgumentException("redirectUri 不能为空");
        }

        String appId = resolveAppId(request.getAppId());
        AlipayOauthScope scope = request.getScope() == null ? AlipayOauthScope.AUTH_USER : request.getScope();

        UrlBuilder urlBuilder = UrlBuilder.ofHttp(AUTH_URL)
                .addQuery("app_id", appId)
                .addQuery("scope", scope.getCode())
                .addQuery("redirect_uri", request.getRedirectUri());

        if (StringUtils.hasText(request.getState())) {
            urlBuilder.addQuery("state", request.getState());
        }
        return urlBuilder.build();
    }

    /**
     * 查询用户授权信息。
     *
     * @param accessToken 用户访问令牌
     * @return 用户授权信息
     */
    @Override
    public AlipayUserInfoShareResponse queryUserInfoShare(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalArgumentException("accessToken 不能为空");
        }

        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        try {
            AlipayUserInfoShareResponse response = execute(request, accessToken);
            if (!response.isSuccess()) {
                log.error("DefaultAliPayOAuth2Service.queryUserInfoShare 查询支付宝用户信息失败, accessToken={}, code={}, msg={}, subCode={}, subMsg={}",
                        accessToken, response.getCode(), response.getMsg(), response.getSubCode(), response.getSubMsg());
                throw AliPayException.QUERY_USER_ERROR;
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("DefaultAliPayOAuth2Service.queryUserInfoShare 查询支付宝用户信息异常, accessToken={}, errCode={}, errMsg={}",
                    accessToken, e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.QUERY_USER_ERROR;
        }
    }

    /**
     * 通过授权码换取令牌。
     *
     * @param authorizationCode 授权码
     * @return OAuth2 令牌信息
     */
    @Override
    public AliPayOauthTokenResult getAccessTokenByCode(String authorizationCode) {
        if (!StringUtils.hasText(authorizationCode)) {
            throw new IllegalArgumentException("authorizationCode 不能为空");
        }
        return getSystemOAuthToken(AlipayOauthGrantType.AUTHORIZATION_CODE, authorizationCode, null);
    }

    /**
     * 通过刷新令牌换取新令牌。
     *
     * @param refreshToken 刷新令牌
     * @return OAuth2 令牌信息
     */
    @Override
    public AliPayOauthTokenResult refreshAccessToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new IllegalArgumentException("refreshToken 不能为空");
        }
        return getSystemOAuthToken(AlipayOauthGrantType.REFRESH_TOKEN, null, refreshToken);
    }

    /**
     * 统一获取 OAuth2 令牌。
     *
     * @param grantType 授权类型
     * @param code 授权码
     * @param refreshToken 刷新令牌
     * @return OAuth2 令牌信息
     */
    private AliPayOauthTokenResult getSystemOAuthToken(AlipayOauthGrantType grantType, String code, String refreshToken) {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType(grantType.getCode());
        request.setCode(code);
        request.setRefreshToken(refreshToken);

        try {
            AlipaySystemOauthTokenResponse response = execute(request);
            if (!response.isSuccess()) {
                log.error("DefaultAliPayOAuth2Service.getSystemOAuthToken 获取支付宝授权访问令牌失败, grantType={}, code={}, refreshToken={}, codeResp={}, msg={}, subCode={}, subMsg={}",
                        grantType.getCode(), code, refreshToken, response.getCode(), response.getMsg(),
                        response.getSubCode(), response.getSubMsg());
                throw AliPayException.REQUEST_TOKEN_ERROR;
            }
            return AliPayOauthTokenResult.builder()
                    .accessToken(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .openId(response.getOpenId())
                    .userId(response.getUserId())
                    .unionId(response.getUnionId())
                    .expiresIn(response.getExpiresIn())
                    .reExpiresIn(response.getReExpiresIn())
                    .authTokenType(response.getAuthTokenType())
                    .authStart(response.getAuthStart())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAliPayOAuth2Service.getSystemOAuthToken 获取支付宝授权访问令牌异常, grantType={}, code={}, refreshToken={}, errCode={}, errMsg={}",
                    grantType.getCode(), code, refreshToken, e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.REQUEST_TOKEN_ERROR;
        }
    }

    private String resolveAppId(String appId) {
        String resolvedAppId = StringUtils.hasText(appId) ? appId : getCurrentConfig().getAppId();
        if (!StringUtils.hasText(resolvedAppId)) {
            throw new IllegalArgumentException("appId 不能为空");
        }
        return resolvedAppId;
    }

    @Override
    protected AlipayClient getAlipayClient() {
        return createAlipayClient();
    }

    @Override
    protected AlipayConfigProvider getAlipayConfigProvider() {
        return provider;
    }
}
