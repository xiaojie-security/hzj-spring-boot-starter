package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 获取类目响应。
 */
public class WechatXcxSubscribeMessageCategoryResponse {

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
     * 类目列表。
     */
    @SerializedName("data")
    public List<WechatXcxSubscribeMessageCategory> data;
}
