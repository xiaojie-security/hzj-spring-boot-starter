package com.hzj.wechat.core.xcx.ad;

import com.hzj.wechat.core.xcx.ad.domain.WechatXcxAdDataRequest;
import com.hzj.wechat.core.xcx.ad.domain.WechatXcxAdDataResponse;
import com.hzj.wechat.core.xcx.ad.domain.WechatXcxAdDataDetailResponse;
import com.hzj.wechat.core.xcx.ad.domain.WechatXcxAdUnitListResponse;
import com.hzj.wechat.core.xcx.ad.domain.WechatXcxAdSettlementResponse;

/**
 * 微信小程序广告数据服务。
 */
public interface WechatXcxAdDataService {

    /**
     * 获取小程序广告汇总数据。
     *
     * @param request 广告汇总数据请求参数
     * @return 广告汇总数据
     */
    WechatXcxAdDataResponse getAdDataSummary(WechatXcxAdDataRequest request);

    /**
     * 获取小程序广告细分数据。
     *
     * @param request 广告细分数据请求参数
     * @return 广告细分数据
     */
    WechatXcxAdDataDetailResponse getAdDataDetail(WechatXcxAdDataRequest request);

    /**
     * 获取小程序广告位清单。
     *
     * @param request 广告位查询请求参数
     * @return 广告位清单
     */
    WechatXcxAdUnitListResponse getAdUnitList(WechatXcxAdDataRequest request);

    /**
     * 获取小程序结算收入数据及结算主体信息。
     *
     * @param request 结算数据请求参数
     * @return 结算收入数据
     */
    WechatXcxAdSettlementResponse getSettlementData(WechatXcxAdDataRequest request);
}
