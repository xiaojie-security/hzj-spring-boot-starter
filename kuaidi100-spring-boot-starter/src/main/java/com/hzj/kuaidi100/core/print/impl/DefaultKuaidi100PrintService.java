package com.hzj.kuaidi100.core.print.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzj.common.utils.JsonUtils;
import com.hzj.kuaidi100.client.Kuaidi100HttpClient;
import com.hzj.kuaidi100.core.common.Kuaidi100ApiResponse;
import com.hzj.kuaidi100.core.common.Kuaidi100CallbackPayload;
import com.hzj.kuaidi100.core.common.Kuaidi100ResultResponse;
import com.hzj.kuaidi100.core.print.Kuaidi100PrintService;
import com.hzj.kuaidi100.core.print.domain.Kuaidi100PrintRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 快递100云打印与自定义打印服务默认实现。
 */
@Slf4j
public class DefaultKuaidi100PrintService implements Kuaidi100PrintService {

    /** 发货单云打印接口地址。 */
    private static final String CLOUD_PRINT_URL = "https://poll.kuaidi100.com/print/billparcels.do";

    /** 自定义模板打印接口地址。 */
    private static final String CUSTOM_PRINT_URL = "https://api.kuaidi100.com/label/order";

    /** 云打印设备状态及电子面单打印接口地址。 */
    private static final String PRINT_DEVICE_STATUS_URL = "https://poll.kuaidi100.com/printapi/printtask.do";

    /** HTTP客户端。 */
    private final Kuaidi100HttpClient httpClient;

    /**
     * 创建打印服务。
     *
     * @param httpClient 快递100 HTTP客户端
     */
    public DefaultKuaidi100PrintService(Kuaidi100HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("Kuaidi100HttpClient 不能为空");
        }
        this.httpClient = httpClient;
    }

    @Override
    public Kuaidi100ApiResponse cloudPrint(Kuaidi100PrintRequest request) {
        requireRequest(request, "DefaultKuaidi100PrintService.cloudPrint");
        String method = isBlank(request.getMethod()) ? "billparcels" : request.getMethod();
        JsonNode raw = httpClient.postBillPrint(CLOUD_PRINT_URL, method,
                request.copyParameters(), request.copySettings());
        return convertResponse(raw);
    }

    @Override
    public Kuaidi100ApiResponse customPrint(Kuaidi100PrintRequest request) {
        return execute(request, CUSTOM_PRINT_URL, "custom", "DefaultKuaidi100PrintService.customPrint");
    }

    @Override
    public Kuaidi100ApiResponse attachmentPrint(Kuaidi100PrintRequest request) {
        requireRequest(request, "DefaultKuaidi100PrintService.attachmentPrint");
        JsonNode raw = httpClient.postMultipart(PRINT_DEVICE_STATUS_URL, "imgOrder", request.getFile(),
                request.copyParameters());
        return convertResponse(raw);
    }

    @Override
    public Kuaidi100ApiResponse instructionPrint(Kuaidi100PrintRequest request) {
        return execute(request, PRINT_DEVICE_STATUS_URL, "printCommand",
                "DefaultKuaidi100PrintService.instructionPrint");
    }

    @Override
    public Kuaidi100ApiResponse reprint(Kuaidi100PrintRequest request) {
        return execute(request, PRINT_DEVICE_STATUS_URL, "printOld",
                "DefaultKuaidi100PrintService.reprint");
    }

    @Override
    public Kuaidi100ResultResponse queryDeviceStatus(Kuaidi100PrintRequest request) {
        if (request == null) {
            log.error("DefaultKuaidi100PrintService.queryDeviceStatus 请求参数为空");
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
        String method = isBlank(request.getMethod()) ? "devstatus" : request.getMethod();
        JsonNode raw = httpClient.postEnterprise(PRINT_DEVICE_STATUS_URL, method,
                request.copyParameters(), true);
        Kuaidi100ResultResponse response = JsonUtils.readValue(raw.toString(), Kuaidi100ResultResponse.class);
        response.setRaw(raw);
        return response;
    }

    @Override
    public Kuaidi100CallbackPayload parseCallback(Map<String, String> formParameters) {
        if (formParameters == null) {
            log.error("DefaultKuaidi100PrintService.parseCallback 回调参数为空");
            throw new IllegalArgumentException("快递100回调参数不能为空");
        }
        Kuaidi100CallbackPayload payload = new Kuaidi100CallbackPayload();
        payload.setSign(formParameters.get("sign"));
        payload.setTaskId(formParameters.get("taskId"));
        payload.setPushType(formParameters.get("pushType"));
        payload.setType(formParameters.get("type"));
        String param = formParameters.get("param");
        if (!isBlank(param)) {
            payload.setParam(httpClient.readJson(param, "DefaultKuaidi100PrintService.parseCallback"));
        }
        return payload;
    }

    private Kuaidi100ApiResponse execute(Kuaidi100PrintRequest request, String endpoint,
                                         String defaultMethod, String action) {
        requireRequest(request, action);
        String method = isBlank(request.getMethod()) ? defaultMethod : request.getMethod();
        JsonNode raw = httpClient.postEnterprise(endpoint, method, request.copyParameters(), true);
        return convertResponse(raw);
    }

    /**
     * 转换通用打印响应。
     *
     * @param raw 原始响应
     * @return 打印响应
     */
    private Kuaidi100ApiResponse convertResponse(JsonNode raw) {
        Kuaidi100ApiResponse response = JsonUtils.readValue(raw.toString(), Kuaidi100ApiResponse.class);
        response.setRaw(raw);
        return response;
    }

    /**
     * 校验打印请求。
     *
     * @param request 打印请求
     * @param action 调用动作
     */
    private void requireRequest(Kuaidi100PrintRequest request, String action) {
        if (request == null) {
            log.error("{} 请求参数为空", action);
            throw new IllegalArgumentException("快递100请求参数不能为空");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
