package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 获取已有模板列表请求参数。
 */
public class WechatXcxSubscribeMessageTemplateListRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建获取已有模板列表请求参数。
     */
    public WechatXcxSubscribeMessageTemplateListRequest() {
        requestPath = "/wxaapi/newtmpl/gettemplate";
        requestMethod = WechatHttpMethod.GET;
    }
}
