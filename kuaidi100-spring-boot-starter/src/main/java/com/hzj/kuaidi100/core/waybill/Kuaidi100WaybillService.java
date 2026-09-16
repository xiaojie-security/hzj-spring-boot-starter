package com.hzj.kuaidi100.core.waybill;

import com.hzj.kuaidi100.core.common.Kuaidi100ApiResponse;
import com.hzj.kuaidi100.core.waybill.domain.Kuaidi100ElectronicWaybillRequest;

/**
 * 快递100电子面单服务。
 */
public interface Kuaidi100WaybillService {

    /**
     * 电子面单下单。
     *
     * @param request 下单请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse createOrder(Kuaidi100ElectronicWaybillRequest request);

    /**
     * 电子面单复打。
     *
     * @param request 复打请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse reprint(Kuaidi100ElectronicWaybillRequest request);

    /**
     * 取消电子面单订单。
     *
     * @param request 取消请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse cancelOrder(Kuaidi100ElectronicWaybillRequest request);
}
