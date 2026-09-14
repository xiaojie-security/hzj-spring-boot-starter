package com.hzj.wechat.core.xcx.subscribe_message.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信订阅消息跳转小程序查看的语言类型。
 */
public enum WechatXcxSubscribeMessageLang {

    /**
     * 简体中文。
     */
    @SerializedName("zh_CN")
    ZH_CN("zh_CN"),

    /**
     * 英文。
     */
    @SerializedName("en_US")
    EN_US("en_US"),

    /**
     * 繁体中文。
     */
    @SerializedName("zh_HK")
    ZH_HK("zh_HK"),

    /**
     * 繁体中文。
     */
    @SerializedName("zh_TW")
    ZH_TW("zh_TW");

    private static final Map<String, WechatXcxSubscribeMessageLang> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSubscribeMessageLang lang : values()) {
            VALUE_MAP.put(lang.value, lang);
        }
    }

    private final String value;

    WechatXcxSubscribeMessageLang(String value) {
        this.value = value;
    }

    /**
     * 获取语言类型字符串值。
     *
     * @return 语言类型字符串值
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的枚举实例。
     *
     * @param value 字符串值，如 "zh_CN"、"en_US"、"zh_HK"、"zh_TW"
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSubscribeMessageLang of(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.get(value);
    }
}
