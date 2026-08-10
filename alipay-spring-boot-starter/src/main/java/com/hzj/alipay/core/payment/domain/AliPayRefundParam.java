package com.hzj.alipay.core.payment.domain;

import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.domain.OpenApiRoyaltyDetailInfoPojo;
import com.alipay.api.domain.RefundGoodsDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付宝交易退款入参。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayRefundParam {

    /**
     * 退款金额。
     */
    private BigDecimal refundAmount;

    /**
     * 商户订单号。
     */
    private String outTradeNo;

    /**
     * 支付宝交易号。
     */
    private String tradeNo;

    /**
     * 退款原因。
     */
    private String refundReason;

    /**
     * 退款请求号。
     */
    private String outRequestNo;

    /**
     * 商户操作员编号。
     */
    private String operatorId;

    /**
     * 收单机构 pid。
     */
    private String orgPid;

    /**
     * 商户门店编号。
     */
    private String storeId;

    /**
     * 商户终端编号。
     */
    private String terminalId;

    /**
     * 查询选项。
     */
    private String[] queryOptions;

    /**
     * 商品明细。
     */
    private GoodsDetail[] goodsDetail;

    /**
     * 退款商品明细。
     */
    private RefundGoodsDetail[] refundGoodsDetail;

    /**
     * 退分账明细。
     */
    private OpenApiRoyaltyDetailInfoPojo[] refundRoyaltyParameters;

    /**
     * 指定垫资退款账号主体。
     */
    private String refundAdvanceAccount;

    /**
     * 指定垫资退款账号类型。
     */
    private String refundAdvanceAccountType;

    /**
     * 指定退款账号。
     */
    private String refundTransOut;

    /**
     * 指定退款账号类型。
     */
    private String refundTransOutType;

    /**
     * 关联结算确认号。
     */
    private String relatedSettleConfirmNo;

    /**
     * 退款币种。
     */
    private String refundCurrency;
}
