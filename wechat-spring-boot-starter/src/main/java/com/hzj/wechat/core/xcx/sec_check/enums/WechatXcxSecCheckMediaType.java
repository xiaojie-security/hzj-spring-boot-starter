package com.hzj.wechat.core.xcx.sec_check.enums;

import com.google.gson.annotations.JsonAdapter;
import com.hzj.wechat.utils.WechatIntegerEnum;
import com.hzj.wechat.utils.WechatIntegerEnumTypeAdapterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信多媒体内容安全检测的多媒体类型。
 */
@JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)
public enum WechatXcxSecCheckMediaType implements WechatIntegerEnum {

    /**
     * 音频。
     */
    AUDIO(1, "音频"),

    /**
     * 图片。
     */
    IMAGE(2, "图片");

    private static final Map<Integer, WechatXcxSecCheckMediaType> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSecCheckMediaType mediaType : values()) {
            VALUE_MAP.put(mediaType.value, mediaType);
        }
    }

    private final int value;

    private final String description;

    WechatXcxSecCheckMediaType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * 获取多媒体类型描述。
     *
     * @return 多媒体类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据整型值获取对应的枚举实例。
     *
     * @param value 多媒体类型值
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSecCheckMediaType of(Integer value) {
        return value == null ? null : VALUE_MAP.get(value);
    }
}
