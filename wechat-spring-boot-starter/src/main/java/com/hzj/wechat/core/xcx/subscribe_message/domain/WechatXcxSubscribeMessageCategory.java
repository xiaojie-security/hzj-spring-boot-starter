package com.hzj.wechat.core.xcx.subscribe_message.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 微信订阅消息类目。
 */
public class WechatXcxSubscribeMessageCategory {

    /**
     * 类目 id，查询公共模板库时需要。
     */
    @SerializedName("id")
    public Integer id;

    /**
     * 类目的中文名。
     */
    @SerializedName("name")
    public String name;
}
