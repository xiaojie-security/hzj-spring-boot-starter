package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 获取模板中的关键词响应。
 */
public class WechatXcxSubscribeMessageKeywordResponse {

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
     * 模板标题列表总数。
     */
    @SerializedName("count")
    public Integer count;

    /**
     * 关键词列表。
     */
    @SerializedName("data")
    public List<WechatXcxSubscribeMessageKeyword> data;
}
