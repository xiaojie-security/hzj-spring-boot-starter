package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckLabel;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckSuggest;

/**
 * 微信内容安全检测详细结果。
 */
public class WechatXcxSecCheckDetail {

    /**
     * 策略类型，如 content_model、keyword。
     */
    @SerializedName("strategy")
    public String strategy;

    /**
     * 错误码，仅当该值为 0 时该项结果有效。
     */
    @SerializedName("errcode")
    public Integer errcode;

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

    /**
     * 命中的自定义关键词。
     */
    @SerializedName("keyword")
    public String keyword;

    /**
     * 0-100，代表置信度，越高代表越有可能属于当前返回的标签。
     */
    @SerializedName("prob")
    public Integer prob;
}
