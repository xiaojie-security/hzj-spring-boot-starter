package com.hzj.kuaidi100.core.query.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzj.common.utils.JsonUtils;
import com.hzj.kuaidi100.client.Kuaidi100HttpClient;
import com.hzj.kuaidi100.core.common.Kuaidi100CallbackPayload;
import com.hzj.kuaidi100.core.common.Kuaidi100SubscriptionResponse;
import com.hzj.kuaidi100.core.query.Kuaidi100QueryService;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100MapTrackQueryRequest;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100MapTrackSubscribeRequest;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100QueryRequest;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100QueryResponse;
import com.hzj.kuaidi100.core.query.domain.Kuaidi100SubscribeRequest;
import com.hzj.kuaidi100.provider.kuaidi100.Kuaidi100RuntimeConfigProvider;
import com.hzj.kuaidi100.provider.kuaidi100.entity.Kuaidi100RuntimeConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 快递100查询与订阅服务默认实现。
 */
@Slf4j
public class DefaultKuaidi100QueryService implements Kuaidi100QueryService {

    /** 实时查询地址。 */
    private static final String QUERY_URL = "https://poll.kuaidi100.com/poll/query.do";

    /** 订阅地址。 */
    private static final String SUBSCRIBE_URL = "https://poll.kuaidi100.com/poll";

    /** 地图轨迹查询地址。 */
    private static final String MAP_QUERY_URL = "https://poll.kuaidi100.com/poll/maptrack.do";

    /** 地图轨迹订阅地址。 */
    private static final String MAP_SUBSCRIBE_URL = "https://poll.kuaidi100.com/pollmap";

    /** HTTP客户端。 */
    private final Kuaidi100HttpClient httpClient;

    /** 运行时配置提供者，可为空。 */
    private final Kuaidi100RuntimeConfigProvider runtimeConfigProvider;

    /**
     * 创建查询服务。
     *
     * @param httpClient 快递100 HTTP客户端
     * @param runtimeConfigProvider 运行时配置提供者
     */
    public DefaultKuaidi100QueryService(Kuaidi100HttpClient httpClient,
                                        Kuaidi100RuntimeConfigProvider runtimeConfigProvider) {
        if (httpClient == null) {
            throw new IllegalArgumentException("Kuaidi100HttpClient 不能为空");
        }
        this.httpClient = httpClient;
        this.runtimeConfigProvider = runtimeConfigProvider;
    }

    @Override
    public Kuaidi100QueryResponse query(Kuaidi100QueryRequest request) {
        requireRequest(request, "DefaultKuaidi100QueryService.query");
        JsonNode raw = httpClient.postCustomer(QUERY_URL, request.toParameters(isIntelligentJudgment()), request.getSignType());
        return convertQueryResponse(raw);
    }

    @Override
    public Kuaidi100SubscriptionResponse subscribe(Kuaidi100SubscribeRequest request) {
        requireRequest(request, "DefaultKuaidi100QueryService.subscribe");
        JsonNode raw = httpClient.postSubscription(SUBSCRIBE_URL,
                request.toParameters(httpClient.getKey(), isIntelligentJudgment()));
        return JsonUtils.readValue(raw.toString(), Kuaidi100SubscriptionResponse.class);
    }

    @Override
    public Kuaidi100QueryResponse queryMapTrack(Kuaidi100MapTrackQueryRequest request) {
        requireRequest(request, "DefaultKuaidi100QueryService.queryMapTrack");
        JsonNode raw = httpClient.postCustomer(MAP_QUERY_URL, request.toParameters(), "MD5");
        return convertQueryResponse(raw);
    }

    @Override
    public Kuaidi100SubscriptionResponse subscribeMapTrack(Kuaidi100MapTrackSubscribeRequest request) {
        requireRequest(request, "DefaultKuaidi100QueryService.subscribeMapTrack");
        JsonNode raw = httpClient.postSubscription(MAP_SUBSCRIBE_URL,
                request.toParameters(httpClient.getKey()));
        return JsonUtils.readValue(raw.toString(), Kuaidi100SubscriptionResponse.class);
    }

    @Override
    public Kuaidi100CallbackPayload parsePush(Map<String, String> formParameters) {
        if (formParameters == null) {
            log.error("DefaultKuaidi100QueryService.parsePush 回调参数为空");
            throw new IllegalArgumentException("快递100回调参数不能为空");
        }
        Kuaidi100CallbackPayload payload = new Kuaidi100CallbackPayload();
        payload.setSign(formParameters.get("sign"));
        payload.setTaskId(formParameters.get("taskId"));
        payload.setPushType(formParameters.get("pushType"));
        payload.setType(formParameters.get("type"));
        String param = formParameters.get("param");
        if (param != null && !param.trim().isEmpty()) {
            payload.setParam(httpClient.readJson(param, "DefaultKuaidi100QueryService.parsePush"));
        }
        return payload;
    }

    private Kuaidi100QueryResponse convertQueryResponse(JsonNode raw) {
        Kuaidi100QueryResponse response = JsonUtils.readValue(raw.toString(), Kuaidi100QueryResponse.class);
        response.setRaw(raw);
        return response;
    }

    private boolean isIntelligentJudgment() {
        if (runtimeConfigProvider == null) {
            return false;
        }
        Kuaidi100RuntimeConfig config = runtimeConfigProvider.getConfig();
        return config != null && config.isIntelligentJudgmentEnabled();
    }

    private void requireRequest(Object request, String action) {
        if (request == null) {
            log.error("{} 请求参数为空", action);
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
    }
}
