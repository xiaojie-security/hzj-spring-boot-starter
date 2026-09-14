package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 获取模板中的关键词请求参数。
 */
public class WechatXcxSubscribeMessageKeywordRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建获取模板中的关键词请求参数。
     */
    public WechatXcxSubscribeMessageKeywordRequest() {
        requestPath = "/wxaapi/newtmpl/getpubtemplatekeywords";
        requestMethod = WechatHttpMethod.GET;
    }

    /**
     * 模板标题 id，可通过获取类目下的公共模板接口获得。
     */
    public String tid;
}
