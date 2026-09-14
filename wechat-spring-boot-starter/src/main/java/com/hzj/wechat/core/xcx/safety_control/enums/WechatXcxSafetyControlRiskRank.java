package com.hzj.wechat.core.xcx.safety_control.enums;

import com.google.gson.annotations.JsonAdapter;
import com.hzj.wechat.utils.WechatIntegerEnum;
import com.hzj.wechat.utils.WechatIntegerEnumTypeAdapterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信用户安全等级。
 *
 * <p>合法值为 0 至 4，数字越大风险越高。</p>
 */
@JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)
public enum WechatXcxSafetyControlRiskRank implements WechatIntegerEnum {

    /**
     * 风险等级 0。
     */
    RANK_0(0, "风险等级 0"),

    /**
     * 风险等级 1。
     */
    RANK_1(1, "风险等级 1"),

    /**
     * 风险等级 2。
     */
    RANK_2(2, "风险等级 2"),

    /**
     * 风险等级 3。
     */
    RANK_3(3, "风险等级 3"),

    /**
     * 风险等级 4。
     */
    RANK_4(4, "风险等级 4");

    private static final Map<Integer, WechatXcxSafetyControlRiskRank> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxSafetyControlRiskRank rank : values()) {
            VALUE_MAP.put(rank.value, rank);
        }
    }

    private final int value;

    private final String description;

    WechatXcxSafetyControlRiskRank(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * 获取等级描述。
     *
     * @return 等级描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据整型值获取对应的枚举实例。
     *
     * @param value 风险等级
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxSafetyControlRiskRank of(Integer value) {
        return value == null ? null : VALUE_MAP.get(value);
    }
}
