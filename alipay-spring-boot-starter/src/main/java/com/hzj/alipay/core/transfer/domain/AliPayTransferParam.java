package com.hzj.alipay.core.transfer.domain;

import com.hzj.alipay.core.transfer.domain.AliPayTransferMultiCurrencyDetail;
import com.hzj.alipay.core.transfer.domain.AliPayTransferParticipant;
import com.hzj.alipay.core.transfer.domain.AliPayTransferSceneReportInfo;
import com.hzj.alipay.core.transfer.domain.AliPayTransferSignData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 单笔转账参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferParam {

    /**
     * 转账业务场景。
     */
    private String bizScene;

    /**
     * 转账业务扩展参数。
     */
    private String businessParams;

    /**
     * 多币种转账信息。
     */
    private AliPayTransferMultiCurrencyDetail mutipleCurrencyDetail;

    /**
     * 转账标题。
     */
    private String orderTitle;

    /**
     * 原支付宝业务单号。
     */
    private String originalOrderId;

    /**
     * 商户转账单号。
     */
    private String outBizNo;

    /**
     * 公用回传参数。
     */
    private String passbackParams;

    /**
     * 收款方信息。
     */
    private AliPayTransferParticipant payeeInfo;

    /**
     * 付款方信息。
     */
    private AliPayTransferParticipant payerInfo;

    /**
     * 转账产品码。
     */
    private String productCode;

    /**
     * 转账备注。
     */
    private String remark;

    /**
     * 特殊场景签名信息。
     */
    private AliPayTransferSignData signData;

    /**
     * 转账金额。
     */
    private BigDecimal transAmount;

    /**
     * 转账场景名称。
     */
    private String transferSceneName;

    /**
     * 转账场景补充上报信息。
     */
    private AliPayTransferSceneReportInfo[] transferSceneReportInfos;

    public AliPayTransferParam(String outBizNo, BigDecimal transAmount, String orderTitle, AliPayTransferParticipant payeeInfo) {
        this.outBizNo = outBizNo;
        this.transAmount = transAmount;
        this.orderTitle = orderTitle;
        this.payeeInfo = payeeInfo;
    }
}
