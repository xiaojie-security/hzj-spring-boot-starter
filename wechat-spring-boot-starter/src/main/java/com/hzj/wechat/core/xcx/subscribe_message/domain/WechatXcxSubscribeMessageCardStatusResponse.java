package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 查询服务卡片状态响应。
 */
public class WechatXcxSubscribeMessageCardStatusResponse {

    /**
     * 微信错误码，0 表示成功。
     */
    @SerializedName("errcode")
    public Integer errcode;

    /**
     * 微信错误信息。
     */
    @SerializedName("errmsg")
    public String errmsg;

    /**
     * 卡片状态。
     */
    @SerializedName("notify_info")
    public WechatXcxSubscribeMessageCardStatus notifyInfo;
}
