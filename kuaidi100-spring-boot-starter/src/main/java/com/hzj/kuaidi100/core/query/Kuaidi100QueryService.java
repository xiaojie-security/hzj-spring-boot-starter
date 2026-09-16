package com.hzj.kuaidi100.core.query;

import com.hzj.kuaidi100.core.common.Kuaidi100CallbackPayload;
import com.hzj.kuaidi100.core.common.Kuaidi100SubscriptionResponse;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100MapTrackQueryRequest;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100MapTrackSubscribeRequest;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100QueryRequest;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100QueryResponse;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100SubscribeRequest;

import java.util.Map;

/**
 * 快递100查询与订阅服务。
 */
public interface Kuaidi100QueryService {

    /**
     * 实时查询物流轨迹。
     *
     * @param request 查询请求
     * @return 查询响应
     */
    Kuaidi100QueryResponse query(Kuaidi100QueryRequest request);

    /**
     * 订阅物流信息推送。
     *
     * @param request 订阅请求
     * @return 订阅响应
     */
    Kuaidi100SubscriptionResponse subscribe(Kuaidi100SubscribeRequest request);

    /**
     * 查询地图轨迹。
     *
     * @param request 地图轨迹查询请求
     * @return 地图轨迹响应
     */
    Kuaidi100QueryResponse queryMapTrack(Kuaidi100MapTrackQueryRequest request);

    /**
     * 订阅地图轨迹推送。
     *
     * @param request 地图轨迹订阅请求
     * @return 订阅响应
     */
    Kuaidi100SubscriptionResponse subscribeMapTrack(Kuaidi100MapTrackSubscribeRequest request);

    /**
     * 解析查询或地图轨迹回调。
     *
     * @param formParameters 回调表单参数
     * @return 回调主体
     */
    Kuaidi100CallbackPayload parsePush(Map<String, String> formParameters);
}
