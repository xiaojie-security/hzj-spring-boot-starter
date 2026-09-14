package com.hzj.wechat.core.xcx.safety_control;

import com.hzj.wechat.core.xcx.safety_control.domain.WechatXcxSafetyControlUserRiskRankRequest;
import com.hzj.wechat.core.xcx.safety_control.domain.WechatXcxSafetyControlUserRiskRankResponse;
import com.hzj.wechat.core.xcx.safety_control.enums.WechatXcxSafetyControlScene;

/**
 * 微信小程序安全风控服务。
 *
 * <p>封装「获取用户安全等级 getUserRiskRank」接口，用于根据提交的用户信息数据获取用户的安全等级，
 * 无需用户授权。</p>
 */
public interface WechatXcxSafetyControlService {

    /**
     * 获取用户安全等级。
     *
     * @param request 获取用户安全等级请求参数
     * @return 用户安全等级
     */
    WechatXcxSafetyControlUserRiskRankResponse getUserRiskRank(
            WechatXcxSafetyControlUserRiskRankRequest request);

    /**
     * 获取用户安全等级。
     * appid 未设置时使用配置提供者的 appid。
     *
     * @param openid   用户的 openid
     * @param scene    场景值
     * @param clientIp 用户访问源 ip
     * @return 用户安全等级
     */
    WechatXcxSafetyControlUserRiskRankResponse getUserRiskRank(String openid,
                                                               WechatXcxSafetyControlScene scene,
                                                               String clientIp);
}
