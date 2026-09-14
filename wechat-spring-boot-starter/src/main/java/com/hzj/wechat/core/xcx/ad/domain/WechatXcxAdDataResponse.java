package com.hzj.wechat.core.xcx.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信小程序广告汇总数据响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatXcxAdDataResponse {

    /**
     * 接口基础响应。
     */
    @SerializedName("base_resp")
    private WechatXcxAdBaseResponse baseResp;

    /**
     * 按广告位和日期返回的汇总明细。
     */
    private List<WechatXcxAdDataItem> list;

    /**
     * 查询范围内的汇总统计数据。
     */
    private WechatXcxAdDataSummary summary;

    /**
     * 当前查询条件下的明细总条数。
     */
    @SerializedName("total_num")
    private Integer totalNum;
}
