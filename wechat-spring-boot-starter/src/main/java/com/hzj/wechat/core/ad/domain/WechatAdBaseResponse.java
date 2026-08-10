package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信广告数据接口基础响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatAdBaseResponse {

    /**
     * 微信广告接口业务错误信息。
     */
    @SerializedName("err_msg")
    private String errMsg;

    /**
     * 微信广告接口业务错误码，0 表示成功。
     */
    private Integer ret;
}
