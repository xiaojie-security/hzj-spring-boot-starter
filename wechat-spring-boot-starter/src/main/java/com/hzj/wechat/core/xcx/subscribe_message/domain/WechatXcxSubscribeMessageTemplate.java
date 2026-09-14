package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageTemplateType;

import java.util.List;

/**
 * 微信订阅消息私有模板。
 */
public class WechatXcxSubscribeMessageTemplate {

    /**
     * 添加至账号下的模板 id，发送订阅消息时所需。
     */
    @SerializedName("priTmplId")
    public String priTmplId;

    /**
     * 模板标题。
     */
    @SerializedName("title")
    public String title;

    /**
     * 模板内容。
     */
    @SerializedName("content")
    public String content;

    /**
     * 模板内容示例。
     */
    @SerializedName("example")
    public String example;

    /**
     * 模板类型。
     */
    @SerializedName("type")
    public WechatXcxSubscribeMessageTemplateType type;

    /**
     * 枚举参数值范围。
     */
    @SerializedName("keywordEnumValueList")
    public List<WechatXcxSubscribeMessageKeywordEnumValue> keywordEnumValueList;
}
