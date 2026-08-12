package com.hzj.wechat.core.transfer.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 微信单号查询转账单请求参数。
 * 用于封装按微信支付转账单号查询转账单详情时所需的路径参数。
 */
public class GetTransferBillByNoRequest extends WechatTransferApiRequest {
    /**
     * 创建按微信单号查询转账单请求参数。
     */
    public GetTransferBillByNoRequest() {
        requestPath = "/v3/fund-app/mch-transfer/transfer-bills/transfer-bill-no/{transfer_bill_no}";
        requestMethod = WechatHttpMethod.GET;
    }
    /**
     * 微信支付转账单号。
     * 该值由微信支付在受理转账后生成。
     */
    @SerializedName("transfer_bill_no")
    @Expose(serialize = false)
    public String transferBillNo;
}
