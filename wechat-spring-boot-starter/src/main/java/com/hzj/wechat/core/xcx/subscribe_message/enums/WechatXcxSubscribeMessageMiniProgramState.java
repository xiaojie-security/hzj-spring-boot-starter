package com.hzj.wechat.core.xcx.subscribe_message.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信订阅消息跳转小程序类型。
 */
public enum WechatXcxSubscribeMessageMiniProgramState {

    /**
     * 开发版。
     */
    @SerializedName("developer")
    DEVELOPER("developer"),

    /**
     * 体验版。
     */
    @SerializedName("trial")
    TRIAL("trial"),

    /**
     * 正式版。
     */
    @SerializedName("formal")
    FORMAL("formal");

    private static final Map<String, WechatXcxSubscribeMessageMiniProgramState> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSubscribeMessageMiniProgramState state : values()) {
            VALUE_MAP.put(state.value, state);
        }
    }

    private final String value;

    WechatXcxSubscribeMessageMiniProgramState(String value) {
        this.value = value;
    }

    /**
     * 获取跳转小程序类型字符串值。
     *
     * @return 跳转小程序类型字符串值
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的枚举实例。
     *
     * @param value 字符串值，如 "developer"、"trial"、"formal"
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSubscribeMessageMiniProgramState of(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.get(value);
    }
}
