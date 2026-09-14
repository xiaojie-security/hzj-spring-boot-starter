package com.hzj.wechat.core.xcx.sec_check.enums;

import com.google.gson.annotations.JsonAdapter;
import com.hzj.wechat.utils.WechatIntegerEnum;
import com.hzj.wechat.utils.WechatIntegerEnumTypeAdapterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信内容安全检测命中的标签枚举。
 */
@JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)
public enum WechatXcxSecCheckLabel implements WechatIntegerEnum {

    /**
     * 正常。
     */
    NORMAL(100, "正常"),

    /**
     * 广告。
     */
    AD(10001, "广告"),

    /**
     * 时政。
     */
    POLITICS(20001, "时政"),

    /**
     * 色情。
     */
    PORN(20002, "色情"),

    /**
     * 辱骂。
     */
    ABUSE(20003, "辱骂"),

    /**
     * 违法犯罪。
     */
    ILLEGAL(20006, "违法犯罪"),

    /**
     * 欺诈。
     */
    FRAUD(20008, "欺诈"),

    /**
     * 低俗。
     */
    VULGAR(20012, "低俗"),

    /**
     * 版权。
     */
    COPYRIGHT(20013, "版权"),

    /**
     * 其他。
     */
    OTHER(21000, "其他");

    private static final Map<Integer, WechatXcxSecCheckLabel> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSecCheckLabel label : values()) {
            VALUE_MAP.put(label.value, label);
        }
    }

    private final int value;

    private final String description;

    WechatXcxSecCheckLabel(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * 获取标签描述。
     *
     * @return 标签描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据整型值获取对应的枚举实例。
     *
     * @param value 标签值
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSecCheckLabel of(Integer value) {
        return value == null ? null : VALUE_MAP.get(value);
    }
}
