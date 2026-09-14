package com.hzj.wechat.core.xcx.safety_control.enums;

import com.google.gson.annotations.JsonAdapter;
import com.hzj.wechat.utils.WechatIntegerEnum;
import com.hzj.wechat.utils.WechatIntegerEnumTypeAdapterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信安全风控场景枚举。
 */
@JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)
public enum WechatXcxSafetyControlScene implements WechatIntegerEnum {

    /**
     * 注册场景。
     */
    REGISTER(0, "注册"),

    /**
     * 营销作弊场景。
     */
    MARKETING_CHEAT(1, "营销作弊"),

    /**
     * UGC 场景。
     */
    UGC(2, "UGC");

    private static final Map<Integer, WechatXcxSafetyControlScene> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSafetyControlScene scene : values()) {
            VALUE_MAP.put(scene.value, scene);
        }
    }

    private final int value;

    private final String description;

    WechatXcxSafetyControlScene(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * 获取场景描述。
     *
     * @return 场景描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据整型值获取对应的枚举实例。
     *
     * @param value 场景值
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSafetyControlScene of(Integer value) {
        return value == null ? null : VALUE_MAP.get(value);
    }
}
