package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 转账参与方标识类型枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayTransferParticipantIdentityType {

    ALIPAY_USER_ID("ALIPAY_USER_ID", "支付宝会员 ID"),
    ALIPAY_LOGON_ID("ALIPAY_LOGON_ID", "支付宝登录号"),
    ALIPAY_OPEN_ID("ALIPAY_OPEN_ID", "支付宝 OpenID"),
    EXPRESS_DC_STFA("EXPRESS_DC_STFA", "对公快捷银行卡"),
    BANKCARD_ACCOUNT("BANKCARD_ACCOUNT", "银行卡账号");

    private final String code;

    private final String message;
}
