package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 多媒体内容安全识别响应。
 * <p>
 * 该接口为异步检测，响应只返回 {@code trace_id}，检测结果会在 30 分钟内推送到消息接收服务器，
 * 推送内容见 {@link WechatXcxSecCheckMediaPushMessage}。
 */
public class WechatXcxSecCheckMediaResponse {

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
     * 唯一请求标识，标记单次请求，用于匹配异步推送结果。
     */
    @SerializedName("trace_id")
    public String traceId;
}
