package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageLang;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageMiniProgramState;

import java.util.Map;

/**
 * 发送订阅消息请求参数。
 */
public class WechatXcxSubscribeMessageSendRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建发送订阅消息请求参数。
     */
    public WechatXcxSubscribeMessageSendRequest() {
        requestPath = "/cgi-bin/message/subscribe/send";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * 接收者（用户）的 openid。
     */
    @SerializedName("touser")
    public String touser;

    /**
     * 所需下发的订阅模板 id。
     */
    @SerializedName("template_id")
    public String templateId;

    /**
     * 点击模板卡片后的跳转页面，仅限本小程序内的页面。
     * 支持携带参数，如 index?foo=bar；不填则模板无跳转。
     */
    @SerializedName("page")
    public String page;

    /**
     * 模板内容，key 为模板关键词（如 phrase3），value 为关键词取值。
     */
    @SerializedName("data")
    public Map<String, WechatXcxSubscribeMessageDataValue> data;

    /**
     * 跳转小程序类型。
     * 未设置时优先使用配置提供者的默认值，仍为空时使用正式版。
     */
    @SerializedName("miniprogram_state")
    public WechatXcxSubscribeMessageMiniProgramState miniprogramState;

    /**
     * 进入小程序查看的语言类型。
     * 未设置时优先使用配置提供者的默认值，仍为空时使用简体中文。
     */
    @SerializedName("lang")
    public WechatXcxSubscribeMessageLang lang;
}
