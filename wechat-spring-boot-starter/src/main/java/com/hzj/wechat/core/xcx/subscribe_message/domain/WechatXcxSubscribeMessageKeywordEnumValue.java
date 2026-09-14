package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 微信订阅消息模板关键词的枚举参数值范围。
 */
public class WechatXcxSubscribeMessageKeywordEnumValue {

    /**
     * 枚举参数的 key。
     */
    @SerializedName("keywordCode")
    public String keywordCode;

    /**
     * 枚举参数值范围列表。
     */
    @SerializedName("enumValueList")
    public List<String> enumValueList;
}
