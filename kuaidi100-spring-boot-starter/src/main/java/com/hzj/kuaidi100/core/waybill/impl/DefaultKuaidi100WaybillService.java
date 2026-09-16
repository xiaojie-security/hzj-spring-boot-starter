package com.hzj.kuaidi100.core.waybill.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzj.common.utils.JsonUtils;
import com.hzj.kuaidi100.client.Kuaidi100HttpClient;
import com.hzj.kuaidi100.core.common.Kuaidi100ApiResponse;
import com.hzj.kuaidi100.core.waybill.Kuaidi100WaybillService;
import com.hzj.kuaidi100.core.waybill.domain.Kuaidi100ElectronicWaybillRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 快递100电子面单服务默认实现。
 */
@Slf4j
public class DefaultKuaidi100WaybillService implements Kuaidi100WaybillService {

    /** 电子面单接口地址。 */
    private static final String LABEL_ORDER_URL = "https://api.kuaidi100.com/label/order";

    /** HTTP客户端。 */
    private final Kuaidi100HttpClient httpClient;

    /**
     * 创建电子面单服务。
     *
     * @param httpClient 快递100 HTTP客户端
     */
    public DefaultKuaidi100WaybillService(Kuaidi100HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("Kuaidi100HttpClient 不能为空");
        }
        this.httpClient = httpClient;
    }

    @Override
    public Kuaidi100ApiResponse createOrder(Kuaidi100ElectronicWaybillRequest request) {
        return execute(request, "order", "DefaultKuaidi100WaybillService.createOrder");
    }

    @Override
    public Kuaidi100ApiResponse reprint(Kuaidi100ElectronicWaybillRequest request) {
        return execute(request, "printOld", "DefaultKuaidi100WaybillService.reprint");
    }

    @Override
    public Kuaidi100ApiResponse cancelOrder(Kuaidi100ElectronicWaybillRequest request) {
        return execute(request, "cancelOrder", "DefaultKuaidi100WaybillService.cancelOrder");
    }

    private Kuaidi100ApiResponse execute(Kuaidi100ElectronicWaybillRequest request,
                                         String defaultMethod, String action) {
        if (request == null) {
            log.error("{} 请求参数为空", action);
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
        String method = isBlank(request.getMethod()) ? defaultMethod : request.getMethod();
        JsonNode raw = httpClient.postEnterprise(LABEL_ORDER_URL, method, request.copyParameters(), true);
        Kuaidi100ApiResponse response = JsonUtils.readValue(raw.toString(), Kuaidi100ApiResponse.class);
        response.setRaw(raw);
        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
