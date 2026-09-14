package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckLabel;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckSuggest;

/**
 * 微信内容安全检测综合结果。
 */
public class WechatXcxSecCheckResult {

    /**
     * 建议，有 risky、pass、review 三种值。
     */
    @SerializedName("suggest")
    public WechatXcxSecCheckSuggest suggest;

    /**
     * 命中标签枚举值。
     */
    @SerializedName("label")
    public WechatXcxSecCheckLabel label;
}
