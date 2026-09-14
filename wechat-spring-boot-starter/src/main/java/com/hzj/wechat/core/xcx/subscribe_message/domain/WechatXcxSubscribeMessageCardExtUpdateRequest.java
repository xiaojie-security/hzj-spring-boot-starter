package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 更新服务卡片扩展信息请求参数。
 */
public class WechatXcxSubscribeMessageCardExtUpdateRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建更新服务卡片扩展信息请求参数。
     */
    public WechatXcxSubscribeMessageCardExtUpdateRequest() {
        requestPath = "/wxa/set_user_notifyext";
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
     * 扩展信息，不同卡片的定义不同。
     */
    @SerializedName("ext_json")
    public String extJson;
}
