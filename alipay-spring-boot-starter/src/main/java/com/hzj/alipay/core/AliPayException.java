package com.hzj.alipay.core;

/**
 * 支付宝业务异常。
 */
public class AliPayException extends RuntimeException {

    /** 创建无消息异常。 */
    public AliPayException() {
    }

    /**
     * 创建指定消息的异常。
     *
     * @param message 异常消息
     */
    public AliPayException(String message) {
        super(message);
    }

    /** 请求支付失败。 */
    public static final AliPayException REQUEST_PAY_ERROR = new AliPayException("请求支付失败");
    /** 交易查询失败。 */
    public static final AliPayException QUERY_ERROR = new AliPayException("交易查询失败");
    /** 订单退款失败。 */
    public static final AliPayException REFUND_ERROR = new AliPayException("订单退款失败");
    /** 查询订单退款结果失败。 */
    public static final AliPayException QUERY_REFUND_ERROR = new AliPayException("查询订单退款结果失败");
    /** 转账失败。 */
    public static final AliPayException TRANSFER_ERROR = new AliPayException("转账失败");
    /** 获取授权访问令牌异常。 */
    public static final AliPayException REQUEST_TOKEN_ERROR = new AliPayException("获取授权访问令牌异常");
    /** 查询用户信息异常。 */
    public static final AliPayException QUERY_USER_ERROR = new AliPayException("查询用户信息异常");
}
