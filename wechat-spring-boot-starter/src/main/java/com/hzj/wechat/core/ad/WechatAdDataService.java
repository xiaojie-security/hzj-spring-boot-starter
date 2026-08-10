package com.hzj.wechat.core.ad;

import com.hzj.wechat.core.ad.domain.WechatAdDataRequest;
import com.hzj.wechat.core.ad.domain.WechatAdDataResponse;
import com.hzj.wechat.core.ad.domain.WechatAdDataDetailResponse;
import com.hzj.wechat.core.ad.domain.WechatAdUnitListResponse;
import com.hzj.wechat.core.ad.domain.WechatAdSettlementResponse;

/**
 * 微信小程序广告数据服务。
 */
public interface WechatAdDataService {

    /**
     * 获取小程序广告汇总数据。
     *
     * @param request 广告汇总数据请求参数
     * @return 广告汇总数据
     */
    WechatAdDataResponse getAdDataSummary(WechatAdDataRequest request);

    /**
     * 获取小程序广告细分数据。
     *
     * @param request 广告细分数据请求参数
     * @return 广告细分数据
     */
    WechatAdDataDetailResponse getAdDataDetail(WechatAdDataRequest request);

    /**
     * 获取小程序广告位清单。
     *
     * @param request 广告位查询请求参数
     * @return 广告位清单
     */
    WechatAdUnitListResponse getAdUnitList(WechatAdDataRequest request);

    /**
     * 获取小程序结算收入数据及结算主体信息。
     *
     * @param request 结算数据请求参数
     * @return 结算收入数据
     */
    WechatAdSettlementResponse getSettlementData(WechatAdDataRequest request);
}
