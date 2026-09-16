package com.hzj.kuaidi100.core.print;

import com.hzj.kuaidi100.core.common.Kuaidi100ApiResponse;
import com.hzj.kuaidi100.core.common.Kuaidi100CallbackPayload;
import com.hzj.kuaidi100.core.common.Kuaidi100ResultResponse;
import com.hzj.kuaidi100.core.print.domain.Kuaidi100PrintRequest;

import java.util.Map;

/**
 * 快递100云打印与自定义打印服务。
 */
public interface Kuaidi100PrintService {

    /**
     * 调用云打印服务。
     *
     * @param request 云打印请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse cloudPrint(Kuaidi100PrintRequest request);

    /**
     * 自定义模板打印。
     *
     * @param request 自定义打印请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse customPrint(Kuaidi100PrintRequest request);

    /**
     * 打印附件。
     *
     * @param request 附件打印请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse attachmentPrint(Kuaidi100PrintRequest request);

    /**
     * 打印指令。
     *
     * @param request 指令打印请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse instructionPrint(Kuaidi100PrintRequest request);

    /**
     * 复打已有任务。
     *
     * @param request 复打请求
     * @return 接口响应
     */
    Kuaidi100ApiResponse reprint(Kuaidi100PrintRequest request);

    /**
     * 查询云打印设备在线状态。
     *
     * @param request 设备状态查询请求
     * @return 设备状态响应
     */
    Kuaidi100ResultResponse queryDeviceStatus(Kuaidi100PrintRequest request);

    /**
     * 解析打印状态、OCR或异常回调。
     *
     * @param formParameters 回调表单参数
     * @return 回调主体
     */
    Kuaidi100CallbackPayload parseCallback(Map<String, String> formParameters);
}
