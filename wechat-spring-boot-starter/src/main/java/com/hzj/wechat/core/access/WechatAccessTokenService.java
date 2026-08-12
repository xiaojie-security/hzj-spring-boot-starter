package com.hzj.wechat.core.access;

import com.hzj.wechat.core.access.domain.WechatAccessTokenResponse;
import com.hzj.wechat.core.access.domain.WechatAccessTokenRequest;

/**
 * 微信接口调用凭据服务。
 */
public interface WechatAccessTokenService {

    /**
     * 获取稳定版接口调用凭据。
     *
     * @return 微信接口调用凭据响应
     */
    default WechatAccessTokenResponse getStableAccessToken() {
        return getStableAccessToken(false);
    }

    /**
     * 获取稳定版接口调用凭据。
     *
     * @param request 获取接口调用凭据请求参数
     * @return 微信接口调用凭据响应
     */
    default WechatAccessTokenResponse getStableAccessToken(WechatAccessTokenRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("WechatAccessTokenRequest 不能为空");
        }
        return getStableAccessToken(request.isForceRefresh());
    }

    /**
     * 获取稳定版接口调用凭据。
     *
     * @param forceRefresh 是否强制刷新凭据
     * @return 微信接口调用凭据响应
     */
    WechatAccessTokenResponse getStableAccessToken(boolean forceRefresh);

    /**
     * 获取 access_token。
     *
     * @return access_token
     */
    default String getAccessToken() {
        return getStableAccessToken().getAccessToken();
    }

    /**
     * 获取 access_token。
     *
     * @param forceRefresh 是否强制刷新凭据
     * @return access_token
     */
    default String getAccessToken(boolean forceRefresh) {
        return getStableAccessToken(forceRefresh).getAccessToken();
    }

    /**
     * 获取 access_token。
     *
     * @param request 获取接口调用凭据请求参数
     * @return access_token
     */
    default String getAccessToken(WechatAccessTokenRequest request) {
        return getStableAccessToken(request).getAccessToken();
    }
}
