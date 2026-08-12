package com.hzj.wechat.core.oauth2;

import com.hzj.wechat.core.oauth2.domain.*;


/**
 * 微信网页授权OAuth2业务接口
 *
 * @author xxx
 */
public interface WechatWebpageOAuth2Service {

    /**
     * 生成微信OAuth2网页授权跳转地址
     *
     * @param request 授权请求参数
     * @return 微信授权URL，前端跳转该地址发起微信授权
     */
    String generateAuthUrl(AuthorizationRequest request);

    /**
     * 通过授权code换取access_token
     * <p>用户授权回调携带code后，调用该接口获取令牌信息</p>
     *
     * @param request code换取token请求参数
     * @return access_token响应结果（包含access_token、refresh_token、openid、过期时间等）
     */
    AccessTokenResponse getAccessTokenByCode(AccessTokenRequest request);

    /**
     * 校验access_token有效性
     *
     * @param request token校验请求参数
     * @return token校验结果，标识token是否有效
     */
    AccessTokenValidateResponse validateAccessToken(AccessTokenValidateRequest request);

    /**
     * 通过access_token获取微信用户信息（scope=snsapi_userinfo）
     * <p>需用户授权snsapi_userinfo权限，可获取昵称、头像、性别等用户公开信息</p>
     *
     * @param request 用户信息查询请求参数
     * @return 微信用户信息响应
     */
    UserInfoResponse queryUserInfoShare(UserInfoRequest request);

    /**
     * 使用refresh_token刷新access_token
     * <p>access_token有效期较短，过期后通过refresh_token重新获取新access_token</p>
     *
     * @param request 刷新token请求参数
     * @return 刷新令牌后的响应结果
     */
    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request);
}
