package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 绑定广告金充值账户请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatBindTransferAccountRequest extends WechatVirtualPaymentRequest {

    /**
     * 充值账户 UID。
     */
    @SerializedName("transfer_account_uid")
    private Long transferAccountUid;

    /**
     * 充值账户主体名称。
     */
    @SerializedName("transfer_account_org_name")
    private String transferAccountOrgName;
}
