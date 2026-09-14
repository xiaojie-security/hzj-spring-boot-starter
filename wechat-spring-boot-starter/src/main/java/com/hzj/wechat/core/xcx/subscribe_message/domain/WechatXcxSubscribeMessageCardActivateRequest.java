package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 激活与更新服务卡片请求参数。
 */
public class WechatXcxSubscribeMessageCardActivateRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建激活与更新服务卡片请求参数。
     */
    public WechatXcxSubscribeMessageCardActivateRequest() {
        requestPath = "/wxa/set_user_notify";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * 用户身份标识符。
     */
    @SerializedName("openid")
    public String openid;

    /**
     * 卡片 id。
     */
    @SerializedName("notify_type")
    public Integer notifyType;

    /**
     * 动态更新令牌。
     */
    @SerializedName("notify_code")
    public String notifyCode;

    /**
     * 卡片状态与状态相关字段，不同卡片的定义不同。
     */
    @SerializedName("content_json")
    public String contentJson;

    /**
     * 微信支付订单号验证字段，仅在将微信支付订单号作为 notify_code 时传入。
     */
    @SerializedName("check_json")
    public String checkJson;
}
