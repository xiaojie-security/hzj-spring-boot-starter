package com.hzj.wechat.utils;

/**
 * 微信接口中取值为整型的枚举。
 *
 * <p>微信开放接口的不少枚举字段在 JSON 中是数字（例如内容安全的 scene=1、
 * 安全风控的 risk_rank=0、人脸核身的 verify_ret=10000）。Gson 默认按枚举名或
 * {@code @SerializedName} 序列化为字符串，会导致接口报参数错误。</p>
 *
 * <p>实现本接口的枚举配合 {@code @JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)}
 * 使用，即可按整型值正确读写。</p>
 */
public interface WechatIntegerEnum {

    /**
     * 获取枚举对应的整型值。
     *
     * @return 枚举对应的整型值
     */
    int getValue();
}
