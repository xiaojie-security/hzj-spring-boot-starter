package com.hzj.wechat.core.xcx.safety_control.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.safety_control.enums.WechatXcxSafetyControlRiskRank;

/**
 * 获取用户安全等级响应。
 */
public class WechatXcxSafetyControlUserRiskRankResponse {

    /**
     * 微信错误码，0 表示成功。
     */
    @SerializedName("errcode")
    public Integer errcode;

    /**
     * 微信错误信息。
     */
    @SerializedName("errmsg")
    public String errmsg;

    /**
     * 用户风险等级，合法值为 0 至 4，数字越大风险越高。
     */
    @SerializedName("risk_rank")
    public WechatXcxSafetyControlRiskRank riskRank;

    /**
     * 唯一请求标识，标记单次请求。
     * 注意：微信官方文档该字段拼写为 unoin_id，此处保持一致。
     */
    @SerializedName("unoin_id")
    public Long unoinId;
}
