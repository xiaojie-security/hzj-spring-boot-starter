package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 选用模板响应。
 */
public class WechatXcxSubscribeMessageTemplateAddResponse {

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
     * 添加至账号下的模板 id，发送订阅消息时所需。
     */
    @SerializedName("priTmplId")
    public String priTmplId;
}
