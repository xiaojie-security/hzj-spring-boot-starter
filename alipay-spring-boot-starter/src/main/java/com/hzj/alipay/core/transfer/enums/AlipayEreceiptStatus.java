package com.hzj.alipay.core.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电子回单状态枚举。
 */
@Getter
@AllArgsConstructor
public enum AlipayEreceiptStatus {

    INIT("INIT", "初始化"),
    PROCESS("PROCESS", "处理中"),
    SUCCESS("SUCCESS", "处理成功"),
    FAIL("FAIL", "处理失败");

    private final String code;

    private final String message;

    public static AlipayEreceiptStatus fromCode(String code) {
        for (AlipayEreceiptStatus value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
