package com.hzj.wechat.core.xcx.face.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信人脸核身证件类型。
 */
public enum WechatXcxFaceCertType {

    /**
     * 身份证。
     */
    @SerializedName("IDENTITY_CARD")
    IDENTITY_CARD("IDENTITY_CARD");

    private static final Map<String, WechatXcxFaceCertType> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxFaceCertType certType : values()) {
            VALUE_MAP.put(certType.value, certType);
        }
    }

    private final String value;

    WechatXcxFaceCertType(String value) {
        this.value = value;
    }

    /**
     * 获取证件类型字符串值。
     *
     * @return 证件类型字符串值
     */
    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取对应的枚举实例。
     *
     * @param value 证件类型值，如 IDENTITY_CARD
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxFaceCertType of(String value) {
        return value == null ? null : VALUE_MAP.get(value);
    }
}
