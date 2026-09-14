package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageCodeState;

/**
 * 微信服务卡片状态。
 */
public class WechatXcxSubscribeMessageCardStatus {

    /**
     * 卡片 id。
     */
    @SerializedName("notify_type")
    public Integer notifyType;

    /**
     * 上次有效推送的卡片状态与状态相关字段，没推送过时为空字符串。
     */
    @SerializedName("content_json")
    public String contentJson;

    /**
     * 动态更新令牌状态。
     */
    @SerializedName("code_state")
    public WechatXcxSubscribeMessageCodeState codeState;

    /**
     * 动态更新令牌过期时间，秒级时间戳。
     */
    @SerializedName("code_expire_time")
    public Long codeExpireTime;
}
