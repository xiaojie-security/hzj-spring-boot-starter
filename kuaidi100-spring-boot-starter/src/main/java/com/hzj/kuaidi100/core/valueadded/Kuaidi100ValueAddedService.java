package com.hzj.kuaidi100.core.valueadded;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzj.kuaidi100.core.common.Kuaidi100ApiResponse;
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

/**
 * 快递100增值服务，不包含短信发送能力。
 */
public interface Kuaidi100ValueAddedService {

    /**
     * 上传电子云签回单文件。
     *
     * @param request 云签文件上传请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse uploadElectronicCloudSignFile(Kuaidi100ElectronicCloudSignRequest request);

    /**
     * 注册电子云签回单文件。
     *
     * @param request 云签注册请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse registerElectronicCloudSign(Kuaidi100ElectronicCloudSignRequest request);

    /**
     * 智能地址解析。
     *
     * @param request 地址解析请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse resolveAddress(Kuaidi100AddressResolutionRequest request);

    /**
     * 智能时效预估。
     *
     * @param request 时效预估请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse estimateTimeliness(Kuaidi100TimelinessRequest request);

    /**
     * 提交拦截改址。
     *
     * @param request 拦截请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse interceptOrder(Kuaidi100ShippingInterceptRequest request);

    /**
     * 取消拦截改址。
     *
     * @param request 取消拦截请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse cancelInterceptOrder(Kuaidi100ShippingInterceptRequest request);

    /**
     * 查询运单附件。
     *
     * @param request 附件查询请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse queryWaybillAttachment(Kuaidi100WaybillAttachmentRequest request);

    /**
     * 智能识别快递单号。
     *
     * @param request 单号识别请求
     * @return 原始识别结果数组或错误对象
     */
    JsonNode recognizeNumber(Kuaidi100NumberRecognitionRequest request);

    /**
     * 判断快递线路可用性。
     *
     * @param request 可用性请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse queryExpressReachability(Kuaidi100ExpressReachabilityRequest request);

    /**
     * 面单 OCR 识别。
     *
     * @param request OCR请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse recognizeWaybillOcr(Kuaidi100WaybillOcrRequest request);

    /**
     * 预估快递价格。
     *
     * @param request 价格预估请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse estimatePrice(Kuaidi100PriceEstimateRequest request);

    /**
     * 注册驿站取件码订阅。
     *
     * @param request 取件码请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse registerPickupCode(Kuaidi100PickupCodeRequest request);

    /**
     * 查询驿站取件码。
     *
     * @param request 取件码请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse queryPickupCode(Kuaidi100PickupCodeRequest request);

    /**
     * 导入物流全链路监控订单。
     *
     * @param request 监控订单请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse monitorOrder(Kuaidi100LogisticsMonitorRequest request);

    /**
     * 回传物流全链路监控订单发货信息。
     *
     * @param request 发货请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse sendOut(Kuaidi100LogisticsMonitorRequest request);
}
