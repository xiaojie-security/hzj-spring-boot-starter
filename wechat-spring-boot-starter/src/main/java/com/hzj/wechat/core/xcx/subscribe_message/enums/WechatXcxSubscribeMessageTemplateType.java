package com.hzj.wechat.core.xcx.subscribe_message.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信订阅消息模板类型。
 */
public enum WechatXcxSubscribeMessageTemplateType {

    /**
     * 一次性订阅。
     */
    @SerializedName("2")
    ONE_TIME(2, "一次性订阅"),

    /**
     * 长期订阅。
     */
    @SerializedName("3")
    LONG_TERM(3, "长期订阅");

    private static final Map<Integer, WechatXcxSubscribeMessageTemplateType> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSubscribeMessageTemplateType type : values()) {
            VALUE_MAP.put(type.value, type);
        }
    }

    private final Integer value;

    private final String description;

    WechatXcxSubscribeMessageTemplateType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取模板类型数值。
     *
     * @return 模板类型数值
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取模板类型描述。
     *
     * @return 模板类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据数值获取对应的枚举实例。
     *
     * @param value 模板类型数值，2 为一次性订阅，3 为长期订阅
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSubscribeMessageTemplateType of(Integer value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.get(value);
    }
}
