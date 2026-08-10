package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子回单申请结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayEreceiptApplyResult {

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
}
