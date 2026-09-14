package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageTemplateType;

/**
 * 微信订阅消息公共模板。
 */
public class WechatXcxSubscribeMessagePublicTemplate {

    /**
     * 模板标题 id。
     */
    @SerializedName("tid")
    public Integer tid;

    /**
     * 模板标题。
     */
    @SerializedName("title")
    public String title;

    /**
     * 模板类型。
     */
    @SerializedName("type")
    public WechatXcxSubscribeMessageTemplateType type;

    /**
     * 模板所属类目 id。
     */
    @SerializedName("categoryId")
    public String categoryId;
}
