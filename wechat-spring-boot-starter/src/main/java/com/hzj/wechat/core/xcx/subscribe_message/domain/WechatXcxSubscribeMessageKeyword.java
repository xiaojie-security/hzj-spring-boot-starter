package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 微信订阅消息模板关键词。
 */
public class WechatXcxSubscribeMessageKeyword {

    /**
     * 关键词 id，选用模板时需要。
     */
    @SerializedName("kid")
    public Integer kid;

    /**
     * 关键词内容。
     */
    @SerializedName("name")
    public String name;

    /**
     * 关键词内容对应的示例。
     */
    @SerializedName("example")
    public String example;

    /**
     * 参数类型。
     */
    @SerializedName("rule")
    public String rule;
}
