package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外卡信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayExtCardInfo {

    /**
     * 外卡户名。
     */
    private String bankAccName;

    /**
     * 开户银行名称。
     */
    private String cardBank;

    /**
     * 开户支行名称。
     */
    private String cardBranch;

    /**
     * 联行号。
     */
    private String cardDeposit;

    /**
     * 开户地址。
     */
    private String cardLocation;

    /**
     * 外卡卡号。
     */
    private String cardNo;

    /**
     * 外卡状态。
     */
    private String status;
}
