package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信小程序广告汇总数据请求参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatAdDataRequest {

    /**
     * 返回页码，从 1 开始。
     */
    @Builder.Default
    private Integer page = 1;

    /**
     * 每页返回数据条数，最大为 90。
     */
    @Builder.Default
    @SerializedName("page_size")
    private Integer pageSize = 90;

    /**
     * 数据开始日期，格式为 yyyy-MM-dd。
     */
    @SerializedName("start_date")
    private String startDate;

    /**
     * 数据结束日期，格式为 yyyy-MM-dd。
     */
    @SerializedName("end_date")
    private String endDate;

    /**
     * 广告位类型名称，不传时返回全部广告位类型数据。
     */
    @SerializedName("ad_slot")
    private String adSlot;

    /**
     * 广告位 ID，可用于查询广告细分数据或广告位清单。
     */
    @SerializedName("ad_unit_id")
    private String adUnitId;
}
