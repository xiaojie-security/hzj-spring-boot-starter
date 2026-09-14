package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 删除模板请求参数。
 */
public class WechatXcxSubscribeMessageTemplateDeleteRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建删除模板请求参数。
     */
    public WechatXcxSubscribeMessageTemplateDeleteRequest() {
        requestPath = "/wxaapi/newtmpl/deltemplate";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * 要删除的模板 id。
     */
    @SerializedName("priTmplId")
    public String priTmplId;
}
