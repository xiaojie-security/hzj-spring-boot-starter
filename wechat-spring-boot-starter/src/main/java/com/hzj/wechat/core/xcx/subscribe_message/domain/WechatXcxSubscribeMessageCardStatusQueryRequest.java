package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 查询服务卡片状态请求参数。
 */
public class WechatXcxSubscribeMessageCardStatusQueryRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建查询服务卡片状态请求参数。
     */
    public WechatXcxSubscribeMessageCardStatusQueryRequest() {
        requestPath = "/wxa/get_user_notify";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * 用户身份标识符。
     */
    @SerializedName("openid")
    public String openid;

    /**
     * 动态更新令牌。
     */
    @SerializedName("notify_code")
    public String notifyCode;

    /**
     * 卡片 id。
     */
    @SerializedName("notify_type")
    public Integer notifyType;
}
