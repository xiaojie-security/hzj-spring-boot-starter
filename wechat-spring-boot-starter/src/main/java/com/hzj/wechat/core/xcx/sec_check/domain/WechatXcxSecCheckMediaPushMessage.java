package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 多媒体内容安全异步检测结果的推送消息。
 *
 * <p>检测结果会在 30 分钟内以 {@code Event=wxa_media_check} 的形式推送到小程序消息接收服务器，
 * 使用方可直接用 {@code WechatPayUtils.fromJson(pushBody, WechatXcxSecCheckMediaPushMessage.class)} 反序列化。</p>
 */
public class WechatXcxSecCheckMediaPushMessage {

    /**
     * 小程序的 username。
     */
    @SerializedName("ToUserName")
    public String toUserName;

    /**
     * 平台推送服务 UserName。
     */
    @SerializedName("FromUserName")
    public String fromUserName;

    /**
     * 发送时间。
     */
    @SerializedName("CreateTime")
    public Long createTime;

    /**
     * 消息类型，默认为 event。
     */
    @SerializedName("MsgType")
    public String msgType;

    /**
     * 事件类型，默认为 wxa_media_check。
     */
    @SerializedName("Event")
    public String event;

    /**
     * 小程序的 appid。
     */
    @SerializedName("appid")
    public String appid;

    /**
     * 任务 id，与发起检测时返回的 trace_id 对应。
     */
    @SerializedName("trace_id")
    public String traceId;

    /**
     * 接口版本，可用于区分接口版本。
     */
    @SerializedName("version")
    public Integer version;

    /**
     * 错误码，仅当该值为 0 时结果有效；该值为 -1008 时表示下载错误，请检查媒体链接是否有效。
     */
    @SerializedName("errcode")
    public Integer errcode;

    /**
     * 错误信息。
     */
    @SerializedName("errmsg")
    public String errmsg;

    /**
     * 综合结果。
     */
    @SerializedName("result")
    public WechatXcxSecCheckResult result;

    /**
     * 详细检测结果。
     */
    @SerializedName("detail")
    public List<WechatXcxSecCheckDetail> detail;
}
