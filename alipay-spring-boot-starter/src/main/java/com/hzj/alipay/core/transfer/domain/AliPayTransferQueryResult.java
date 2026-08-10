package com.hzj.alipay.core.transfer.domain;

import com.hzj.alipay.core.transfer.enums.AlipayTransferStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转账查询结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferQueryResult {

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
     * 预计到账时间。
     */
    private String arrivalTimeEnd;

    /**
     * 扣款账单信息。
     */
    private String deductBillInfo;

    /**
     * 业务错误码。
     */
    private String errorCode;

    /**
     * 失败机构错误码。
     */
    private String failInstErrorCode;

    /**
     * 失败机构名称。
     */
    private String failInstName;

    /**
     * 失败机构原因。
     */
    private String failInstReason;

    /**
     * 失败原因。
     */
    private String failReason;

    /**
     * 入账结算流水号。
     */
    private String inflowSettleSerialNo;

    /**
     * 手续费金额。
     */
    private String orderFee;

    /**
     * 支付宝转账单据号。
     */
    private String orderId;

    /**
     * 商户转账单号。
     */
    private String outBizNo;

    /**
     * 回传参数。
     */
    private String passbackParams;

    /**
     * 支付时间。
     */
    private String payDate;

    /**
     * 支付宝支付资金流水号。
     */
    private String payFundOrderId;

    /**
     * 收款方 OpenID。
     */
    private String receiverOpenId;

    /**
     * 收款方支付宝用户 ID。
     */
    private String receiverUserId;

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
     * 子单据错误码。
     */
    private String subOrderErrorCode;

    /**
     * 子单据失败原因。
     */
    private String subOrderFailReason;

    /**
     * 子单据状态。
     */
    private String subOrderStatus;

    /**
     * 转账子状态。
     */
    private String subStatus;

    /**
     * 转账金额。
     */
    private String transAmount;

    /**
     * 转账账单信息。
     */
    private String transferBillInfo;
}
