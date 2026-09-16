package com.hzj.kuaidi100.core.order;

import com.hzj.kuaidi100.core.common.Kuaidi100CallbackPayload;
import com.hzj.kuaidi100.core.common.Kuaidi100ResultResponse;
import com.hzj.kuaidi100.core.order.domain.Kuaidi100OrderImportRequest;

import java.util.Map;

/**
 * 快递100电商平台订单导入服务。
 */
public interface Kuaidi100OrderImportService {

    /**
     * 获取店铺授权超链接。
     *
     * @param request 授权请求
     * @return 授权响应
     */
    Kuaidi100ResultResponse getShopAuthorizeUrl(Kuaidi100OrderImportRequest request);

    /**
     * 提交销售订单获取任务。
     *
     * @param request 订单任务请求
     * @return 任务响应
     */
    Kuaidi100ResultResponse submitOrderTask(Kuaidi100OrderImportRequest request);

    /**
     * 提交售后订单获取任务。
     *
     * @param request 售后任务请求
     * @return 任务响应
     */
    Kuaidi100ResultResponse submitRefundOrderTask(Kuaidi100OrderImportRequest request);

    /**
     * 回传快递单号并更新订单发货状态。
     *
     * @param request 发货请求
     * @return 接口响应
     */
    Kuaidi100ResultResponse sendLogistics(Kuaidi100OrderImportRequest request);

    /**
     * 解析店铺授权、订单任务或订单结果回调。
     *
     * @param formParameters 回调表单参数
     * @return 回调主体
     */
    Kuaidi100CallbackPayload parseCallback(Map<String, String> formParameters);
}
