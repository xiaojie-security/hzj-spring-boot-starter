package com.hzj.wechat.core.xcx.subscribe_message.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信服务卡片动态更新令牌状态。
 */
public enum WechatXcxSubscribeMessageCodeState {

    /**
     * 正常。
     */
    @SerializedName("0")
    NORMAL(0, "正常"),

    /**
     * 有风险。
     */
    @SerializedName("1")
    RISKY(1, "有风险"),

    /**
     * 异常。
     */
    @SerializedName("2")
    ABNORMAL(2, "异常"),

    /**
     * 用户拒收本次 code。
     */
    @SerializedName("10")
    USER_REJECT(10, "用户拒收本次 code");

    private static final Map<Integer, WechatXcxSubscribeMessageCodeState> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSubscribeMessageCodeState state : values()) {
            VALUE_MAP.put(state.value, state);
        }
    }

    private final Integer value;

    private final String description;

    WechatXcxSubscribeMessageCodeState(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取令牌状态数值。
     *
     * @return 令牌状态数值
     */
    public Integer getValue() {
        return value;
    }

    /**
     * 获取令牌状态描述。
     *
     * @return 令牌状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据数值获取对应的枚举实例。
     *
     * @param value 令牌状态数值
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSubscribeMessageCodeState of(Integer value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.get(value);
    }
}
