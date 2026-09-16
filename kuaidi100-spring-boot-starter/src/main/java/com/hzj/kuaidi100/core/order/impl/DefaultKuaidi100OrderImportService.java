package com.hzj.kuaidi100.core.order.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzj.common.utils.JsonUtils;
import com.hzj.kuaidi100.client.Kuaidi100HttpClient;
import com.hzj.kuaidi100.core.common.Kuaidi100CallbackPayload;
import com.hzj.kuaidi100.core.common.Kuaidi100ResultResponse;
import com.hzj.kuaidi100.core.order.Kuaidi100OrderImportService;
import com.hzj.kuaidi100.core.order.domain.Kuaidi100OrderImportRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 快递100电商平台订单导入服务默认实现。
 */
@Slf4j
public class DefaultKuaidi100OrderImportService implements Kuaidi100OrderImportService {

    /** 店铺授权地址。 */
    private static final String SHOP_AUTHORIZE_URL = "https://api.kuaidi100.com/ent/shop/authorize";

    /** 销售订单获取任务地址。 */
    private static final String ORDER_TASK_URL = "https://api.kuaidi100.com/ent/order/task";

    /** 售后订单获取任务地址。 */
    private static final String REFUND_ORDER_TASK_URL = "https://api.kuaidi100.com/ent/refundOrder/task";

    /** 发货回传地址。 */
    private static final String LOGISTICS_SEND_URL = "https://api.kuaidi100.com/ent/logistics/send";

    /** HTTP客户端。 */
    private final Kuaidi100HttpClient httpClient;

    /**
     * 创建订单导入服务。
     *
     * @param httpClient 快递100 HTTP客户端
     */
    public DefaultKuaidi100OrderImportService(Kuaidi100HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("Kuaidi100HttpClient 不能为空");
        }
        this.httpClient = httpClient;
    }

    @Override
    public Kuaidi100ResultResponse getShopAuthorizeUrl(Kuaidi100OrderImportRequest request) {
        return execute(request, SHOP_AUTHORIZE_URL, "DefaultKuaidi100OrderImportService.getShopAuthorizeUrl");
    }

    @Override
    public Kuaidi100ResultResponse submitOrderTask(Kuaidi100OrderImportRequest request) {
        return execute(request, ORDER_TASK_URL, "DefaultKuaidi100OrderImportService.submitOrderTask");
    }

    @Override
    public Kuaidi100ResultResponse submitRefundOrderTask(Kuaidi100OrderImportRequest request) {
        return execute(request, REFUND_ORDER_TASK_URL,
                "DefaultKuaidi100OrderImportService.submitRefundOrderTask");
    }

    @Override
    public Kuaidi100ResultResponse sendLogistics(Kuaidi100OrderImportRequest request) {
        return execute(request, LOGISTICS_SEND_URL, "DefaultKuaidi100OrderImportService.sendLogistics");
    }

    @Override
    public Kuaidi100CallbackPayload parseCallback(Map<String, String> formParameters) {
        if (formParameters == null) {
            log.error("DefaultKuaidi100OrderImportService.parseCallback 回调参数为空");
            throw new IllegalArgumentException("快递100回调参数不能为空");
        }
        Kuaidi100CallbackPayload payload = new Kuaidi100CallbackPayload();
        payload.setSign(formParameters.get("sign"));
        String param = formParameters.get("param");
        if (!isBlank(param)) {
            payload.setParam(httpClient.readJson(param, "DefaultKuaidi100OrderImportService.parseCallback"));
        }
        return payload;
    }

    private Kuaidi100ResultResponse execute(Kuaidi100OrderImportRequest request,
                                            String endpoint, String action) {
        if (request == null) {
            log.error("{} 请求参数为空", action);
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
        JsonNode raw = httpClient.postEnterprise(endpoint, null, request.copyParameters(), false);
        Kuaidi100ResultResponse response = JsonUtils.readValue(raw.toString(), Kuaidi100ResultResponse.class);
        response.setRaw(raw);
        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
