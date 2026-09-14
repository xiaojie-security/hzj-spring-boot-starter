package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 获取类目请求参数。
 */
public class WechatXcxSubscribeMessageCategoryRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建获取类目请求参数。
     */
    public WechatXcxSubscribeMessageCategoryRequest() {
        requestPath = "/wxaapi/newtmpl/getcategory";
        requestMethod = WechatHttpMethod.GET;
    }
}
