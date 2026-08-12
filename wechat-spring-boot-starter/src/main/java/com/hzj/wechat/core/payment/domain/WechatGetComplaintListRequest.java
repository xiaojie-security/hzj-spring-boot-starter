package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取投诉列表请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatGetComplaintListRequest extends WechatVirtualPaymentRequest {
    public WechatGetComplaintListRequest() { super(WechatVirtualPaymentApi.GET_COMPLAINT_LIST); }

    /**
     * 筛选开始日期。
     */
    @SerializedName("begin_date")
    private String beginDate;

    /**
     * 筛选结束日期。
     */
    @SerializedName("end_date")
    private String endDate;

    /**
     * 筛选偏移量。
     */
    private Integer offset;

    /**
     * 最多返回条数。
     */
    private Integer limit;
}
