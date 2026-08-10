package com.hzj.alipay.core.payment.domain;

import com.alipay.api.domain.ExtUserInfo;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.domain.RoyaltyInfo;
import com.alipay.api.domain.SettleInfo;
import com.alipay.api.domain.SubMerchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付宝统一支付入参。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayPaymentParam {

    /**
     * 商户订单号。
     */
    private String outTradeNo;

    /**
     * 订单总金额，单位元。
     */
    private BigDecimal totalAmount;

    /**
     * 订单标题。
     */
    private String subject;

    /**
     * 订单描述。
     */
    private String body;

    /**
     * 异步通知地址。
     */
    private String notifyUrl;

    /**
     * 同步跳转地址。
     */
    private String returnUrl;

    /**
     * 用户中途退出返回地址，仅 H5 支付使用。
     */
    private String quitUrl;

    /**
     * 产品码。
     */
    private String productCode;

    /**
     * 订单绝对超时时间。
     */
    private String timeExpire;

    /**
     * 订单相对超时时间。
     */
    private String timeoutExpress;

    /**
     * 卖家支付宝用户 ID。
     */
    private String sellerId;

    /**
     * 商户门店编号。
     */
    private String storeId;

    /**
     * 商户原始订单号。
     */
    private String merchantOrderNo;

    /**
     * 回传参数。
     */
    private String passbackParams;

    /**
     * 商户业务透传参数。
     */
    private String businessParams;

    /**
     * 优惠参数。
     */
    private String promoParams;

    /**
     * 商品主类型。
     */
    private String goodsType;

    /**
     * 指定支付渠道。
     */
    private String enablePayChannels;

    /**
     * 禁用支付渠道。
     */
    private String disablePayChannels;

    /**
     * 指定单通道。
     */
    private String specifiedChannel;

    /**
     * 页面支付请求来源地址。
     */
    private String requestFromUrl;

    /**
     * 页面支付集成方式。
     */
    private String integrationType;

    /**
     * PC 扫码支付模式。
     */
    private Integer qrPayMode;

    /**
     * 自定义二维码宽度。
     */
    private Long qrcodeWidth;

    /**
     * H5 支付授权令牌。
     */
    private String authToken;

    /**
     * 预创建买家支付宝账号。
     */
    private String buyerLogonId;

    /**
     * 预创建码类型。
     */
    private String codeType;

    /**
     * 预创建商户操作员编号。
     */
    private String operatorId;

    /**
     * 预创建商户机具终端编号。
     */
    private String terminalId;

    /**
     * 预创建指定支付方式。
     */
    private String paymentType;

    /**
     * 预创建可打折金额。
     */
    private BigDecimal discountableAmount;

    /**
     * 预创建不可打折金额。
     */
    private BigDecimal undiscountableAmount;

    /**
     * 商品明细。
     */
    private GoodsDetail[] goodsDetail;

    /**
     * 查询选项。
     */
    private String[] queryOptions;

    /**
     * 外部指定买家。
     */
    private ExtUserInfo extUserInfo;

    /**
     * 业务扩展参数。
     */
    private ExtendParams extendParams;

    /**
     * 二级商户信息。
     */
    private SubMerchant subMerchant;

    /**
     * 结算信息。
     */
    private SettleInfo settleInfo;

    /**
     * 分账信息。
     */
    private RoyaltyInfo royaltyInfo;

    public AliPayPaymentParam(String outTradeNo, BigDecimal totalAmount, String subject) {
        this.outTradeNo = outTradeNo;
        this.totalAmount = totalAmount;
        this.subject = subject;
    }
}
