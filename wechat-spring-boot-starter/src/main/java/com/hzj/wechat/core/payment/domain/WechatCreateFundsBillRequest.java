package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建广告金充值单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatCreateFundsBillRequest extends WechatVirtualPaymentRequest {

    /**
     * 充值金额，单位为分。
     */
    @SerializedName("transfer_amount")
    private Long transferAmount;

    /**
     * 充值账户 UID。
     */
    @SerializedName("transfer_account_uid")
    private Long transferAccountUid;

    /**
     * 充值账户名称。
     */
    @SerializedName("transfer_account_name")
    private String transferAccountName;

    /**
     * 充值账户服务商账号 ID。
     */
    @SerializedName("transfer_account_agency_id")
    private Long transferAccountAgencyId;

    /**
     * 商户请求唯一 ID。
     */
    @SerializedName("request_id")
    private String requestId;

    /**
     * 广告金结算周期开始时间。
     */
    @SerializedName("settle_begin")
    private Long settleBegin;

    /**
     * 广告金结算周期结束时间。
     */
    @SerializedName("settle_end")
    private Long settleEnd;

    /**
     * 是否授权广告数据。
     */
    @SerializedName("authorize_advertise")
    private Integer authorizeAdvertise;

    /**
     * 广告金发放原因。
     */
    @SerializedName("fund_type")
    private Integer fundType;
}
