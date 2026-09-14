package com.hzj.wechat.core.xcx.ad.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信小程序广告位清单响应。
 */
@Data
@NoArgsConstructor
public class WechatXcxAdUnitListResponse {

    /**
     * 接口基础响应。
     */
    @SerializedName("base_resp")
    private WechatXcxAdBaseResponse baseResp;

    /**
     * 广告位清单。
     */
    @SerializedName("ad_unit")
    private List<WechatXcxAdUnitInfo> adUnit;

    /**
     * 返回总条数。
     */
    @SerializedName("total_num")
    private Integer totalNum;
}
