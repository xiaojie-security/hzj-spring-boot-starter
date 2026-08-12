package com.hzj.wechat.core.transfer.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 解除免确认收款授权请求参数。
 */
public class CloseAuthorizationRequest extends WechatTransferApiRequest {
    /**
     * 创建关闭授权请求参数。
     */
    public CloseAuthorizationRequest() {
        requestPath = "/v3/fund-app/mch-transfer/user-confirm-authorization/out-authorization-no/{out_authorization_no}/close";
        requestMethod = WechatHttpMethod.POST;
    }
    /**
     * 商户授权单号。
     * 用于唯一标识待解除的授权记录。
     */
    @SerializedName("out_authorization_no")
    @Expose(serialize = false)
    public String outAuthorizationNo;
}
