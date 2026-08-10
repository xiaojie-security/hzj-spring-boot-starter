package com.hzj.wechat.core.qrcode.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public enum WechatXcxEnvVersion {

    @SerializedName("release")
    RELEASE("release"),
    @SerializedName("trial")
    TRIAL("trial"),
    @SerializedName("develop")
    DEVELOP("develop");

    private final String value;

    private static final Map<String, WechatXcxEnvVersion> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxEnvVersion version : values()) {
            VALUE_MAP.put(version.value, version);
        }
    }

    WechatXcxEnvVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的枚举实例。
     *
     * @param value 字符串值，如 "release"、"trial"、"develop"
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxEnvVersion of(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.get(value);
    }
}
