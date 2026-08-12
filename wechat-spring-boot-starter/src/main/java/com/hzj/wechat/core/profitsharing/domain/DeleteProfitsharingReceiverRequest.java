package com.hzj.wechat.core.profitsharing.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.profitsharing.enums.ProfitsharingReceiverType;

/**
 * 删除分账接收方请求参数。
 */
public class DeleteProfitsharingReceiverRequest extends WechatProfitsharingApiRequest {
    /**
     * 删除分账接收方请求参数。
     */
    public DeleteProfitsharingReceiverRequest() {
        requestPath = "/v3/profitsharing/receivers/delete";
        requestMethod = com.hzj.wechat.core.enums.WechatHttpMethod.POST;
    }
    /**
     * 商户应用 AppID。
     * 服务层会在发起请求前自动注入。
     */
    @SerializedName("appid")
    public String appid;

    /**
     * 分账接收方类型。
     */
    @SerializedName("type")
    public ProfitsharingReceiverType type;

    /**
     * 分账接收方账号。
     */
    @SerializedName("account")
    public String account;
}
