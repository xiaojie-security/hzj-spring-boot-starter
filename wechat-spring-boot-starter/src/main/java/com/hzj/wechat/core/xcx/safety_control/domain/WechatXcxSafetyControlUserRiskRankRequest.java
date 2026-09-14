package com.hzj.wechat.core.xcx.safety_control.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.safety_control.enums.WechatXcxSafetyControlScene;

/**
 * 获取用户安全等级请求参数。
 */
public class WechatXcxSafetyControlUserRiskRankRequest extends WechatXcxSafetyControlApiRequest {

    /**
     * 创建获取用户安全等级请求参数。
     */
    public WechatXcxSafetyControlUserRiskRankRequest() {
        requestPath = "/wxa/getuserriskrank";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * 小程序 appid。
     * 未设置时使用配置提供者的 appid。
     */
    @SerializedName("appid")
    public String appid;

    /**
     * 用户的 openid。
     */
    @SerializedName("openid")
    public String openid;

    /**
     * 场景值：0 注册，1 营销作弊，2 UGC。
     */
    @SerializedName("scene")
    public WechatXcxSafetyControlScene scene;

    /**
     * 用户手机号。
     */
    @SerializedName("mobile_no")
    public String mobileNo;

    /**
     * 用户访问源 ip。
     */
    @SerializedName("client_ip")
    public String clientIp;

    /**
     * 用户邮箱地址。
     */
    @SerializedName("email_address")
    public String emailAddress;

    /**
     * 额外补充信息。
     */
    @SerializedName("extended_info")
    public String extendedInfo;

    /**
     * 是否测试调用，false 正式调用，true 测试调用。
     * 未设置时使用配置提供者的默认值，仍为空时使用 false。
     */
    @SerializedName("is_test")
    public Boolean isTest;
}
