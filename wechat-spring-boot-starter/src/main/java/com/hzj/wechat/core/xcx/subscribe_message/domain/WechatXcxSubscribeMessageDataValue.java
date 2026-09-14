package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 订阅消息模板关键词取值。
 */
public class WechatXcxSubscribeMessageDataValue {

    /**
     * 关键词取值。
     */
    @SerializedName("value")
    public String value;

    /**
     * 创建关键词取值。
     */
    public WechatXcxSubscribeMessageDataValue() {
    }

    /**
     * 创建关键词取值。
     *
     * @param value 关键词取值
     */
    public WechatXcxSubscribeMessageDataValue(String value) {
        this.value = value;
    }

    /**
     * 创建关键词取值。
     *
     * @param value 关键词取值
     * @return 关键词取值对象
     */
    public static WechatXcxSubscribeMessageDataValue of(String value) {
        return new WechatXcxSubscribeMessageDataValue(value);
    }
}
