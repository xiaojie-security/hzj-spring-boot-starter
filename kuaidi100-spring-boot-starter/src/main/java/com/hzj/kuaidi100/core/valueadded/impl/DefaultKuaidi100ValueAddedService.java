package com.hzj.kuaidi100.core.valueadded.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzj.common.utils.JsonUtils;
import com.hzj.kuaidi100.client.Kuaidi100HttpClient;
import com.hzj.kuaidi100.core.common.Kuaidi100ApiResponse;
import com.hzj.kuaidi100.core.valueadded.Kuaidi100ValueAddedService;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100AddressResolutionRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100ElectronicCloudSignRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100ExpressReachabilityRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100LogisticsMonitorRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100NumberRecognitionRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100PickupCodeRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100PriceEstimateRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100ShippingInterceptRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100TimelinessRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100WaybillAttachmentRequest;
import com.hzj.kuaidi100.core.valueadded.domain.Kuaidi100WaybillOcrRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 快递100增值服务默认实现，不包含短信发送能力。
 */
@Slf4j
public class DefaultKuaidi100ValueAddedService implements Kuaidi100ValueAddedService {

    /** 智能地址解析接口地址。 */
    private static final String ADDRESS_RESOLUTION_URL = "https://api.kuaidi100.com/address/resolution";

    /** 电子面单聚合接口地址。 */
    private static final String LABEL_ORDER_URL = "https://api.kuaidi100.com/label/order";

    /** 快递可用性接口地址。 */
    private static final String REACHABLE_URL = "https://api.kuaidi100.com/reachable.do";

    /** 面单 OCR 接口地址。 */
    private static final String OCR_URL = "https://api.kuaidi100.com/elec/detocr";

    /** 智能单号识别接口地址。 */
    private static final String NUMBER_RECOGNITION_URL = "https://www.kuaidi100.com/autonumber/auto";

    /** 取件码注册接口地址。 */
    private static final String PICKUP_REGISTER_URL = "https://api.kuaidi100.com/pickupCode/register";

    /** 取件码查询接口地址。 */
    private static final String PICKUP_QUERY_URL = "https://api.kuaidi100.com/pickupCode/query";

    /** 物流监控接口地址。 */
    private static final String LOGISTICS_MONITOR_URL = "https://api.kuaidi100.com/logistics/monitor/api/order";

    /** 电子云签文件上传地址。 */
    private static final String CLOUD_SIGN_UPLOAD_URL = "https://api.kuaidi100.com/thirdApi/file/upload";

    /** HTTP客户端。 */
    private final Kuaidi100HttpClient httpClient;

    /**
     * 创建增值服务。
     *
     * @param httpClient 快递100 HTTP客户端
     */
    public DefaultKuaidi100ValueAddedService(Kuaidi100HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("Kuaidi100HttpClient 不能为空");
        }
        this.httpClient = httpClient;
    }

    @Override
    public Kuaidi100ApiResponse uploadElectronicCloudSignFile(Kuaidi100ElectronicCloudSignRequest request) {
        if (request == null) {
            log.error("DefaultKuaidi100ValueAddedService.uploadElectronicCloudSignFile 请求参数为空");
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
        JsonNode raw = httpClient.postMultipart(CLOUD_SIGN_UPLOAD_URL, request.getFile(), request.copyParameters());
        return convertResponse(raw);
    }

    @Override
    public Kuaidi100ApiResponse registerElectronicCloudSign(Kuaidi100ElectronicCloudSignRequest request) {
        return executeEnterprise(request, LABEL_ORDER_URL, "register", true,
                "DefaultKuaidi100ValueAddedService.registerElectronicCloudSign");
    }

    @Override
    public Kuaidi100ApiResponse resolveAddress(Kuaidi100AddressResolutionRequest request) {
        return executeKeyOnly(request, ADDRESS_RESOLUTION_URL, "DefaultKuaidi100ValueAddedService.resolveAddress");
    }

    @Override
    public Kuaidi100ApiResponse estimateTimeliness(Kuaidi100TimelinessRequest request) {
        return executeEnterprise(request, LABEL_ORDER_URL, "time", true,
                "DefaultKuaidi100ValueAddedService.estimateTimeliness");
    }

    @Override
    public Kuaidi100ApiResponse interceptOrder(Kuaidi100ShippingInterceptRequest request) {
        return executeEnterprise(request, LABEL_ORDER_URL, "interceptOrder", true,
                "DefaultKuaidi100ValueAddedService.interceptOrder");
    }

    @Override
    public Kuaidi100ApiResponse cancelInterceptOrder(Kuaidi100ShippingInterceptRequest request) {
        return executeEnterprise(request, LABEL_ORDER_URL, "cancelInterceptOrder", true,
                "DefaultKuaidi100ValueAddedService.cancelInterceptOrder");
    }

    @Override
    public Kuaidi100ApiResponse queryWaybillAttachment(Kuaidi100WaybillAttachmentRequest request) {
        return executeEnterprise(request, LABEL_ORDER_URL, "backOrder", true,
                "DefaultKuaidi100ValueAddedService.queryWaybillAttachment");
    }

    @Override
    public JsonNode recognizeNumber(Kuaidi100NumberRecognitionRequest request) {
        if (request == null || isBlank(request.getNumber())) {
            log.error("DefaultKuaidi100ValueAddedService.recognizeNumber 单号为空, request={}", request);
            throw new IllegalArgumentException("快递单号不能为空");
        }
        return httpClient.getNumberRecognition(NUMBER_RECOGNITION_URL, request.getNumber());
    }

    @Override
    public Kuaidi100ApiResponse queryExpressReachability(Kuaidi100ExpressReachabilityRequest request) {
        return executeEnterprise(request, REACHABLE_URL, "reachable", true,
                "DefaultKuaidi100ValueAddedService.queryExpressReachability");
    }

    @Override
    public Kuaidi100ApiResponse recognizeWaybillOcr(Kuaidi100WaybillOcrRequest request) {
        return executeKeyOnly(request, OCR_URL, "DefaultKuaidi100ValueAddedService.recognizeWaybillOcr");
    }

    @Override
    public Kuaidi100ApiResponse estimatePrice(Kuaidi100PriceEstimateRequest request) {
        return executeEnterprise(request, LABEL_ORDER_URL, "price", true,
                "DefaultKuaidi100ValueAddedService.estimatePrice");
    }

    @Override
    public Kuaidi100ApiResponse registerPickupCode(Kuaidi100PickupCodeRequest request) {
        return executeEnterprise(request, PICKUP_REGISTER_URL, "pickupCodeRegister", true,
                "DefaultKuaidi100ValueAddedService.registerPickupCode");
    }

    @Override
    public Kuaidi100ApiResponse queryPickupCode(Kuaidi100PickupCodeRequest request) {
        return executeEnterprise(request, PICKUP_QUERY_URL, "pickupCodeQuery", true,
                "DefaultKuaidi100ValueAddedService.queryPickupCode");
    }

    @Override
    public Kuaidi100ApiResponse monitorOrder(Kuaidi100LogisticsMonitorRequest request) {
        return executeEnterprise(request, LOGISTICS_MONITOR_URL, "orderExport", true,
                "DefaultKuaidi100ValueAddedService.monitorOrder");
    }

    @Override
    public Kuaidi100ApiResponse sendOut(Kuaidi100LogisticsMonitorRequest request) {
        return executeEnterprise(request, LOGISTICS_MONITOR_URL, "sendOut", true,
                "DefaultKuaidi100ValueAddedService.sendOut");
    }

    private Kuaidi100ApiResponse executeEnterprise(com.hzj.kuaidi100.core.common.Kuaidi100EnterpriseRequest request,
                                                   String endpoint, String defaultMethod,
                                                   boolean includeTimestamp, String action) {
        if (request == null) {
            log.error("{} 请求参数为空", action);
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
        String method = isBlank(request.getMethod()) ? defaultMethod : request.getMethod();
        JsonNode raw = httpClient.postEnterprise(endpoint, method, request.copyParameters(), includeTimestamp);
        return convertResponse(raw);
    }

    private Kuaidi100ApiResponse executeKeyOnly(com.hzj.kuaidi100.core.common.Kuaidi100EnterpriseRequest request,
                                                String endpoint, String action) {
        if (request == null) {
            log.error("{} 请求参数为空", action);
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
        JsonNode raw = httpClient.postKeyOnly(endpoint, request.copyParameters());
        return convertResponse(raw);
    }

    private Kuaidi100ApiResponse convertResponse(JsonNode raw) {
        Kuaidi100ApiResponse response = JsonUtils.readValue(raw.toString(), Kuaidi100ApiResponse.class);
        response.setRaw(raw);
        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
