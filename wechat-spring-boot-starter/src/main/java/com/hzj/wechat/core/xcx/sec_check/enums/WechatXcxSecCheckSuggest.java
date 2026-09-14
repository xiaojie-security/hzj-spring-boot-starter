package com.hzj.wechat.core.xcx.sec_check.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信内容安全检测建议结果。
 */
public enum WechatXcxSecCheckSuggest {

    /**
     * 疑似违规，建议拦截。
     */
    @SerializedName("risky")
    RISKY("risky"),

    /**
     * 正常，建议放行。
     */
    @SerializedName("pass")
    PASS("pass"),

    /**
     * 建议人工复审。
     */
    @SerializedName("review")
    REVIEW("review");

    private static final Map<String, WechatXcxSecCheckSuggest> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSecCheckSuggest suggest : values()) {
            VALUE_MAP.put(suggest.value, suggest);
        }
    }

    private final String value;

    WechatXcxSecCheckSuggest(String value) {
        this.value = value;
    }

    /**
     * 获取建议结果字符串值。
     *
     * @return 建议结果字符串值
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的枚举实例。
     *
     * @param value 建议结果值，如 risky、pass、review
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSecCheckSuggest of(String value) {
        return value == null ? null : VALUE_MAP.get(value);
    }

    /**
     * 判断是否建议放行。
     *
     * @return 为 pass 时返回 true
     */
    public boolean isPass() {
        return this == PASS;
    }

    /**
     * 判断是否建议拦截。
     *
     * @return 为 risky 时返回 true
     */
    public boolean isRisky() {
        return this == RISKY;
    }
}
