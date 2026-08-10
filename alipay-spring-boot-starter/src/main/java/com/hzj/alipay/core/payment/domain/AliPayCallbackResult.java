package com.hzj.alipay.core.payment.domain;

import cn.hutool.core.collection.CollUtil;
import org.jetbrains.annotations.Contract;

import java.util.Map;

/**
 * 支付宝异步回调结果。
 */
public final class AliPayCallbackResult {

    /** 支付宝回调原始参数。 */
    private final Map<String, String> callbackResult;
    /** 商户订单号。 */
    private final String outTradeNo;
    /** 支付宝交易号。 */
    private final String tradeNo;
    /** 订单总金额。 */
    private final String totalAmount;
    /** 实收金额。 */
    private final String receiptAmount;
    /** 交易状态。 */
    private final String tradeStatus;
    /** 应用 ID。 */
    private final String appId;
    /** 卖家支付宝用户号。 */
    private final String sellerId;

    /**
     * 创建支付宝回调结果。
     *
     * @param callbackResult 支付宝回调参数
     */
    public AliPayCallbackResult(Map<String, String> callbackResult) {
        this.callbackResult = callbackResult;
        this.outTradeNo = callbackResult.getOrDefault("out_trade_no", "");
        this.tradeNo = callbackResult.getOrDefault("trade_no", "");
        this.totalAmount = callbackResult.getOrDefault("total_amount", "");
        this.receiptAmount = callbackResult.getOrDefault("receipt_amount", "");
        this.tradeStatus = callbackResult.getOrDefault("trade_status", "");
        this.appId = callbackResult.getOrDefault("app_id", "");
        this.sellerId = callbackResult.getOrDefault("seller_id", "");
    }

    /**
     * 获取原始回调参数。
     *
     * @param paramsKey 参数名
     * @return 参数值
     */
    public String get(String paramsKey) {
        return callbackResult.get(paramsKey);
    }

    /**
     * 判断回调参数是否为空。
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return CollUtil.isEmpty(callbackResult);
    }

    /**
     * 获取卖家支付宝用户号。
     *
     * @return 卖家支付宝用户号
     */
    @Contract(pure = true)
    public String getSellerId() { return sellerId; }

    /**
     * 获取应用 ID。
     *
     * @return 应用 ID
     */
    @Contract(pure = true)
    public String getAppId() { return appId; }

    /**
     * 获取交易状态。
     *
     * @return 交易状态
     */
    @Contract(pure = true)
    public String getTradeStatus() { return tradeStatus; }

    /**
     * 获取实收金额。
     *
     * @return 实收金额
     */
    @Contract(pure = true)
    public String getReceiptAmount() { return receiptAmount; }

    /**
     * 获取订单总金额。
     *
     * @return 订单总金额
     */
    @Contract(pure = true)
    public String getTotalAmount() { return totalAmount; }

    /**
     * 获取支付宝交易号。
     *
     * @return 支付宝交易号
     */
    @Contract(pure = true)
    public String getTradeNo() { return tradeNo; }

    /**
     * 获取商户订单号。
     *
     * @return 商户订单号
     */
    @Contract(pure = true)
    public String getOutTradeNo() { return outTradeNo; }
}
