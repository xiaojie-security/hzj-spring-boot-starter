package com.hzj.alipay.core.transfer.domain;

import com.hzj.alipay.core.transfer.enums.AlipayEreceiptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子回单状态查询结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayEreceiptQueryResult {

    /**
     * 是否请求成功。
     */
    private boolean success;

    /**
     * 支付宝接口名。
     */
    private String apiMethod;

    /**
     * 支付宝响应码。
     */
    private String code;

    /**
     * 支付宝响应描述。
     */
    private String msg;

    /**
     * 支付宝子响应码。
     */
    private String subCode;

    /**
     * 支付宝子响应描述。
     */
    private String subMsg;

    /**
     * 电子回单文件 ID。
     */
    private String fileId;

    /**
     * 电子回单状态枚举。
     */
    private AlipayEreceiptStatus status;

    /**
     * 电子回单状态码。
     */
    private String statusCode;

    /**
     * 电子回单下载地址。
     */
    private String downloadUrl;

    /**
     * 失败原因说明。
     */
    private String errorMessage;
}
