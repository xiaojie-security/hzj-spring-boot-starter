package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子回单申请参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayEreceiptApplyParam {

    /**
     * 账单用户标识。
     */
    private String billUserId;

    /**
     * 业务键。
     */
    private String key;

    /**
     * 回单类型。
     */
    private String type;
}
