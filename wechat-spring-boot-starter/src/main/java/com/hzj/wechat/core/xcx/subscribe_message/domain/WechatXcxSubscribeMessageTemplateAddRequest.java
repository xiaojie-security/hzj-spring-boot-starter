package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

import java.util.List;

/**
 * 选用模板请求参数。
 */
public class WechatXcxSubscribeMessageTemplateAddRequest extends WechatXcxSubscribeMessageApiRequest {

    /**
     * 创建选用模板请求参数。
     */
    public WechatXcxSubscribeMessageTemplateAddRequest() {
        requestPath = "/wxaapi/newtmpl/addtemplate";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * 模板标题 id，可通过获取类目下的公共模板接口获得。
     */
    @SerializedName("tid")
    public String tid;

    /**
     * 模板关键词列表，顺序可自由组合，最少 2 个、最多 5 个。
     */
    @SerializedName("kidList")
    public List<Integer> kidList;

    /**
     * 服务场景描述，15 个字以内。
     */
    @SerializedName("sceneDesc")
    public String sceneDesc;
}
