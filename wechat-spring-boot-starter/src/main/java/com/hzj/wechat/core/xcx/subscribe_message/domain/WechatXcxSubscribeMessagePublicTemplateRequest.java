package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 获取类目下的公共模板请求参数。
 */
public class WechatXcxSubscribeMessagePublicTemplateRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建获取类目下的公共模板请求参数。
     */
    public WechatXcxSubscribeMessagePublicTemplateRequest() {
        requestPath = "/wxaapi/newtmpl/getpubtemplatetitles";
        requestMethod = WechatHttpMethod.GET;
    }

    /**
     * 类目 id，多个类目使用逗号隔开。
     */
    public String ids;

    /**
     * 分页起始位置，从 0 开始计数。
     */
    public Integer start = 0;

    /**
     * 每次拉取的记录条数，最大为 30。
     */
    public Integer limit = 30;
}
