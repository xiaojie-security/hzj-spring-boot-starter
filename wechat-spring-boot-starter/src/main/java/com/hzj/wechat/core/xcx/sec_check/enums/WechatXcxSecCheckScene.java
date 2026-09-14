package com.hzj.wechat.core.xcx.sec_check.enums;

import com.google.gson.annotations.JsonAdapter;
import com.hzj.wechat.utils.WechatIntegerEnum;
import com.hzj.wechat.utils.WechatIntegerEnumTypeAdapterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信内容安全检测场景枚举。
 */
@JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)
public enum WechatXcxSecCheckScene implements WechatIntegerEnum {

    /**
     * 资料。
     */
    PROFILE(1, "资料"),

    /**
     * 评论。
     */
    COMMENT(2, "评论"),

    /**
     * 论坛。
     */
    FORUM(3, "论坛"),

    /**
     * 社交日志。
     */
    SOCIAL_LOG(4, "社交日志");

    private static final Map<Integer, WechatXcxSecCheckScene> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSecCheckScene scene : values()) {
            VALUE_MAP.put(scene.value, scene);
        }
    }

    private final int value;

    private final String description;

    WechatXcxSecCheckScene(int value, String description) {
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
    public static WechatXcxSecCheckScene of(Integer value) {
        return value == null ? null : VALUE_MAP.get(value);
    }
}
