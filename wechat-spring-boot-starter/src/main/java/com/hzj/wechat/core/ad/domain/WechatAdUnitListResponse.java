package com.hzj.wechat.core.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信小程序广告位清单响应。
 */
@Data
@NoArgsConstructor
public class WechatAdUnitListResponse {

    /**
     * 接口基础响应。
     */
    @SerializedName("base_resp")
    private WechatAdBaseResponse baseResp;

    /**
     * 广告位清单。
     */
    @SerializedName("ad_unit")
    private List<WechatAdUnitInfo> adUnit;

    /**
     * 返回总条数。
     */
    @SerializedName("total_num")
    private Integer totalNum;
}
