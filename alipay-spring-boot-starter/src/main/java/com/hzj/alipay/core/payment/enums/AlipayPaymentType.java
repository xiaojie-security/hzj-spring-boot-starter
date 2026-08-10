package com.hzj.alipay.core.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支付宝支付类型。
 */
@Getter
@RequiredArgsConstructor
public enum AlipayPaymentType {

    PRECREATE("alipay.trade.precreate"),
    APP("alipay.trade.app.pay"),
    WAP("alipay.trade.wap.pay"),
    PAGE("alipay.trade.page.pay");

    private final String apiMethod;
}
