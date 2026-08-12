package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.payment.enums.FundFlowBillAccountType;
import com.hzj.wechat.core.payment.enums.TarType;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 申请资金账单请求参数。
 */
public class FundFlowBillRequest extends WechatPaymentApiRequest {
    /**
     * 创建资金账单请求参数。
     */
    public FundFlowBillRequest() {
        requestPath = "/v3/bill/fundflowbill";
        requestMethod = WechatHttpMethod.GET;
    }
    /**
     * 账单日期。
     * 格式为 YYYY-MM-DD。
     */
    @SerializedName("bill_date")
    @Expose(serialize = false)
    public String billDate;

    /**
     * 资金账户类型。
     */
    @SerializedName("account_type")
    @Expose(serialize = false)
    public FundFlowBillAccountType accountType;

    /**
     * 压缩格式。
     */
    @SerializedName("tar_type")
    @Expose(serialize = false)
    public TarType tarType;
}
