package com.hzj.alipay.core.transfer.domain;

import com.hzj.alipay.core.transfer.enums.AlipayTransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单笔转账结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferResult {

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
     * 转账金额。
     */
    private String amount;

    /**
     * 拉起链接。
     */
    private String link;

    /**
     * 支付宝转账单据号。
     */
    private String orderId;

    /**
     * 商户转账单号。
     */
    private String outBizNo;

    /**
     * 支付宝支付资金流水号。
     */
    private String payFundOrderId;

    /**
     * 结算流水号。
     */
    private String settleSerialNo;

    /**
     * 转账状态枚举。
     */
    private AlipayTransferStatus status;

    /**
     * 转账状态码。
     */
    private String statusCode;

    /**
     * 转账子状态。
     */
    private String subStatus;

    /**
     * 转账完成时间。
     */
    private String transDate;
}
