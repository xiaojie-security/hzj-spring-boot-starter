package com.hzj.wechat.core.transfer.enums;

/**
 * 微信商家转账类型。
 */
public enum WechatTransferType {

    /**
     * 授权后转账。
     */
    AFTER_AUTHORIZATION("/v3/fund-app/mch-transfer/transfer-bills/transfer"),

    /**
     * 自动授权转账。
     */
    AUTO_APPROVAL("/v3/fund-app/mch-transfer/transfer-bills/pre-transfer-with-authorization");

    /**
     * 微信支付 API 请求路径。
     */
    private final String requestPath;

    WechatTransferType(String requestPath) {
        this.requestPath = requestPath;
    }

    /**
     * 获取微信支付 API 请求路径。
     *
     * @return 请求路径
     */
    public String getRequestPath() {
        return requestPath;
    }
}
