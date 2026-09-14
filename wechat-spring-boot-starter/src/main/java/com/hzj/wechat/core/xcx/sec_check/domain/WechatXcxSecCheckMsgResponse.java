package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文本内容安全识别响应。
 */
public class WechatXcxSecCheckMsgResponse {

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
     * 详细检测结果。
     */
    @SerializedName("detail")
    public List<WechatXcxSecCheckDetail> detail;

    /**
     * 唯一请求标识，标记单次请求。
     */
    @SerializedName("trace_id")
    public String traceId;

    /**
     * 综合结果。
     */
    @SerializedName("result")
    public WechatXcxSecCheckResult result;
}
